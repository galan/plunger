package de.galan.plunger.command.kafka;

import static de.galan.commons.util.Sugar.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractPutCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Writes messages to a destination on a Kafka broker.
 */
public class KafkaPutCommand extends AbstractPutCommand {

	//parameter: timeout
	//parameter: key
	//parameter: groupId

	private Producer<String, String> producer;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaUtils.brokers(pa.getTarget()));
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		producer = new KafkaProducer<>(props);
	}


	@Override
	protected void sendMessage(PlungerArguments pa, Message message, long count) throws CommandException {
		try {
			String topic = pa.getTarget().getDestination();
			producer.send(new ProducerRecord<String, String>(topic, getKey(message, pa), message.getBody())).get();
		}
		catch (InterruptedException | ExecutionException ex) {
			throw new CommandException("Failed sending record: " + ex.getMessage(), ex);
		}
	}


	private String getKey(Message message, PlungerArguments pa) {
		String targetKey = trimToNull(pa.getTarget().getParameterValue("key"));
		if (targetKey == null && pa.getTarget().containsParameter("key")) {
			return null; // "key=" user will overwrite keys from message with empty key
		}
		return optional(targetKey).orElseGet(() -> trimToNull(message.getPropertyString("key")));
	}


	@Override
	protected void close() {
		if (producer != null) {
			producer.close();
		}
	}

}
