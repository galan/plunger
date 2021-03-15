package de.galan.plunger.command.kafka;

import static de.galan.commons.util.Sugar.*;
import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.StringSerializer;

import com.google.common.primitives.Ints;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractPutCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Writes messages to a destination on a Kafka broker.
 */
public class KafkaPutCommand extends AbstractPutCommand {

	private final DecoderFactory decoderFactory = new DecoderFactory();
	private Producer<String, Object> producer;
	private Schema schema = null;
	private CommandException lastError;
	private boolean transactional;
	private boolean sendAsync;
	private long numAcked = 0;
	private long numSend = 0;

	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);

		transactional = determineTransaction(pa);
		sendAsync = determineAsync(pa);

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaUtils.brokers(pa.getTarget()));
		props.put(ProducerConfig.ACKS_CONFIG, determineAcksConfig(pa));
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 100_000);
		props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
		props.put(ProducerConfig.LINGER_MS_CONFIG, 50);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		if (determineTransaction(pa)) {
			transactional = true;
			props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());
			props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, determineTransactionTimeout(pa));
			props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
		}
		else {
			props.put(ProducerConfig.RETRIES_CONFIG, 0);
		}

		Integer maxRequestSize = determineMaxRequestSize(pa);
		if (maxRequestSize != null) {
			props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, Integer.toString(maxRequestSize));
		}
		// If a schema registry is provided, we assume that the topic contains Avro
		String schemaRegistry = AvroUtils.determineSchemaRegistry(pa);
		if (isNotBlank(schemaRegistry)) {
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
			props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
			schema = AvroUtils.getSchema(schemaRegistry, pa.getTarget().getDestination());
		}

		producer = new KafkaProducer<>(props);

		if (transactional) {
			producer.initTransactions();
			producer.beginTransaction();
		}
	}


	@Override
	public void onError(CommandException exception) {
		if (transactional && !closed) {
			producer.abortTransaction();
		}
	}


	/**
	 * Returns the maxRequestSize url argument, which could overwrite the default kakfa value for 'max.request.size'.
	 */
	private Integer determineMaxRequestSize(PlungerArguments pa) {
		String param = pa.getTarget().getParameterValue("maxRequestSize");
		return isNotBlank(param) ? Ints.tryParse(param) : null;
	}


	private String determineAcksConfig(PlungerArguments pa) {
		String param = pa.getCommandArgument("acks");
		return isNotBlank(param) ? param : "all";
	}


	private boolean determineAsync(PlungerArguments pa) {
		String param = pa.getTarget().getParameterValue("async");
		return isNotBlank(param) && Boolean.parseBoolean(param);
	}


	private boolean determineTransaction(PlungerArguments pa) {
		String param = pa.getTarget().getParameterValue("tx");
		return isNotBlank(param) && Boolean.parseBoolean(param);
	}


	private int determineTransactionTimeout(PlungerArguments pa) {
		String param = pa.getTarget().getParameterValue("txTimeout");
		if (isBlank(param)) {
			return 300_000;
		}

		Integer timeout = Ints.tryParse(param);
		if (timeout == null) {
			throw new IllegalArgumentException("invalid timeout value");
		}
		return timeout;
	}


	@Override
	protected void sendMessage(PlungerArguments pa, Message message, long count) throws CommandException {
		Headers headers = mapHeader(message);
		String topic = pa.getTarget().getDestination();

		numSend++;
		if (schema == null) {
			sendMessagePlain(pa, message, headers, topic);
		}
		else {
			sendMessageAvro(pa, message, headers, topic);
		}

		if (lastError != null) {
			throw lastError;
		}
	}


	private void sendMessagePlain(PlungerArguments pa, Message message, Headers headers, String topic) throws CommandException {
		if (sendAsync) {
			producer.send(new ProducerRecord<>(topic, null, getKey(message, pa), message.getBody(), headers), (metadata, exception) -> {
				if (exception != null) {
					lastError = new CommandException("Failed sending record: " + exception.getMessage(), exception);
					return;
				}
				numAcked++;
			});
		}
		else {
			try {
				producer.send(new ProducerRecord<>(topic, null, getKey(message, pa), message.getBody(), headers)).get();
				numAcked++;
			}
			catch (InterruptedException | ExecutionException ex) {
				throw new CommandException("Failed sending record: " + ex.getMessage(), ex);
			}
		}
	}


	private void sendMessageAvro(PlungerArguments pa, Message message, Headers headers, String topic) throws CommandException {
		GenericRecord genericRecord;
		try {
			String jsonBody = message.getBody();
			Decoder decoder = decoderFactory.jsonDecoder(schema, jsonBody);
			DatumReader<GenericData.Record> reader = new GenericDatumReader<>(schema);
			genericRecord = reader.read(null, decoder);
		}
		catch (IOException e) {
			throw new CommandException("Could not serialize Avro: " + e.getMessage(), e);
		}

		if (sendAsync) {
			producer.send(new ProducerRecord<>(topic, null, getKey(message, pa), genericRecord, headers), (metadata, exception) -> {
				if (exception != null) {
					lastError = new CommandException("Failed sending record: " + exception.getMessage(), exception);
					return;
				}
				numAcked++;
			});
		}
		else {
			try {
				producer.send(new ProducerRecord<>(topic, null, getKey(message, pa), genericRecord, headers)).get();
				numAcked++;
			}
			catch (InterruptedException | ExecutionException ex) {
				throw new CommandException("Failed sending record: " + ex.getMessage(), ex);
			}
		}
	}


	private Headers mapHeader(Message message) {
		Headers headers = new RecordHeaders();
		if (message.getProperties() != null) {
			for (Entry<String, Object> entry : message.getProperties().entrySet()) {
				headers.add(new RecordHeader(entry.getKey(), entry.getValue().toString().getBytes(UTF_8)));
			}
		}
		return headers;
	}


	private String getKey(Message message, PlungerArguments pa) {
		String targetKey = trimToNull(pa.getTarget().getParameterValue("key"));
		if (targetKey == null && pa.getTarget().containsParameter("key")) {
			return null; // "key=" user will overwrite keys from message with empty key
		}
		return optional(targetKey).orElseGet(() -> trimToNull(message.getPropertyString("kafka.key")));
	}


	@Override
	protected void close() {
		// wait for all acks
		synchronized (this) {
			while(numAcked < numSend) {
				try {
					wait(100);
				}
				catch (InterruptedException e) {
					Output.error("Unexpected interruption: " + e.getMessage());
				}
			}
		}

		if (transactional && !closed) {
			producer.commitTransaction();
		}
		if (producer != null) {
			producer.close();
		}
	}

}
