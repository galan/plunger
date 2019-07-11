package de.galan.plunger.command.kafka;

import static de.galan.commons.util.Sugar.*;
import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.google.common.base.StandardSystemProperty;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import de.galan.commons.time.Durations;
import de.galan.commons.time.Instants;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCatCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;


/**
 * Retrieves messages from a Kafka broker.
 */
public class KafkaCatCommand extends AbstractCatCommand {

	private KafkaConsumer<String, Object> consumer;
	private Iterator<ConsumerRecord<String, Object>> recordIterator;

	int timeout = 1000;
	String groupId;
	String autoOffsetReset;
	private String clientId;
	private boolean commit;

	private Schema schema = null;
	private EncoderFactory encoderFactory = new EncoderFactory();


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);
		clientId = "plunger-" + StandardSystemProperty.USER_NAME.value() + "-" + System.currentTimeMillis();
		groupId = KafkaUtils.groupId(pa.getTarget());
		autoOffsetReset = optional(pa.getTarget().getParameterValue("autoOffsetReset")).orElse("earliest");
		commit = pa.containsCommandArgument("r");
	}


	@Override
	protected void beforeFirstMessage(PlungerArguments pa) throws CommandException {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaUtils.brokers(pa.getTarget()));
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
		props.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG, "true");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, Long.toString(determineMaxPollRecords(pa)));

		Integer maxPartitionFetchBytes = determineMaxPartitionFetchBytes(pa);
		if (maxPartitionFetchBytes != null) {
			props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, Integer.toString(maxPartitionFetchBytes));
		}

		// If the schema Registry is provided, we assume the format is Avro
		String schemaRegistry = AvroUtils.determineSchemaRegistry(pa);
		if (isNotBlank(schemaRegistry)) {
			props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
			schema = AvroUtils.getSchema(schemaRegistry, pa.getTarget().getDestination());
		}

		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(pa.getTarget().getDestination()));
		String timeoutDuration = pa.getTarget().getParameterValue("timeout");
		if (isNotBlank(timeoutDuration)) {
			timeout = Durations.dehumanize(timeoutDuration).intValue();
		}
	}


	/**
	 * Returns the maxPartitionFetchBytes url argument, which could overwrite the default kakfa value for
	 * 'max.partition.fetch.bytes'.
	 */
	private Integer determineMaxPartitionFetchBytes(PlungerArguments pa) {
		String param = pa.getTarget().getParameterValue("maxPartitionFetchBytes");
		if (isNotBlank(param)) {
			return Ints.tryParse(param);
		}
		return null;
	}


	/**
	 * Returns the "maxPollRecords" url argument, which will override the default of 1 for "max.poll.records". if the
	 * size is larger then the limit "-n", it will be reduced to this.
	 */
	private Long determineMaxPollRecords(PlungerArguments pa) {
		Long maxPollRecords = Longs.tryParse(optional(pa.getTarget().getParameterValue("maxPollRecords")).orElse("1"));
		Long limit = pa.getCommandArgumentLong("n");
		if (limit != null && limit < maxPollRecords) {
			maxPollRecords = limit;
		}
		return maxPollRecords;
	}


	@Override
	protected Message getNextMessage(PlungerArguments pa) throws CommandException {
		Message result = null;
		if (recordIterator == null || !recordIterator.hasNext()) {
			ConsumerRecords<String, Object> records = consumer.poll(timeout);
			recordIterator = records.iterator();
		}

		if (recordIterator != null && recordIterator.hasNext()) {
			ConsumerRecord<String, Object> record = recordIterator.next();
			Message msg = new Message();
			msg.setBody(schema == null ? provideMessagePlain(record) : provideMessageAvro(record));
			if (!pa.containsCommandArgument("p")) { // exclude properties or not
				msg.putProperty("kafka.key", record.key());
				msg.putProperty("kafka.offset", record.offset());
				msg.putProperty("kafka.partition", record.partition());
				if (record.timestampType() != null && !record.timestampType().equals(TimestampType.NO_TIMESTAMP_TYPE)) {
					msg.putProperty("kafka.timestamp", Instants.from(Instants.instant(record.timestamp())).toStringUtc());
					msg.putProperty("kafka.timestamp_type", record.timestampType().toString());
				}

				for (Header header: record.headers()) {
					msg.putProperty(header.key(), new String(header.value(), UTF_8));
				}

			}
			result = msg;
		}
		if (commit && recordIterator != null && result != null && !recordIterator.hasNext()) {
			consumer.commitSync();
		}

		return result;
	}


	private String provideMessagePlain(ConsumerRecord<String, Object> record) {
		return (String)record.value();
	}


	private String provideMessageAvro(ConsumerRecord<String, Object> record) throws CommandException {
		String result = null;
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
		try {
			Encoder encoder = encoderFactory.jsonEncoder(schema, byteStream);
			writer.write((GenericRecord)record.value(), encoder);
			encoder.flush();
			result = byteStream.toString();
		}
		catch (IOException e) {
			throw new CommandException("Could not deserialize into Avro: " + e.getMessage(), e);
		}
		return result;
	}


	@Override
	protected boolean isSystemHeader(String headerName) {
		// kafka does not provider own header information, only payload. Meta-data is provided as header instead.
		return startsWith(headerName, "kafka.");
	}


	@Override
	protected void close() {
		if (consumer != null) {
			consumer.unsubscribe();
			consumer.close();
		}
	}

}
