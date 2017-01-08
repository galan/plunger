package de.galan.plunger.command.kafka;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.common.serialization.StringDeserializer;

import de.galan.commons.time.Instants;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCatCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Retrieves messages from a Kafka broker.
 */
public class KafkaCatCommand extends AbstractCatCommand {

	private KafkaConsumer<String, String> consumer;
	private Iterator<ConsumerRecord<String, String>> recordIterator;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);

	}


	@Override
	protected void beforeFirstMessage(PlungerArguments pa) throws CommandException {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, pa.getTarget().getHost() + ":" + pa.getTarget().getPort());
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "plunger-" + UUID.randomUUID().toString());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(pa.getTarget().getDestination()));
	}


	@Override
	protected Message getNextMessage(PlungerArguments pa) throws CommandException {
		// pa.containsCommandArgument("r") // has no use in kafka
		Message result = null;
		if (recordIterator == null || !recordIterator.hasNext()) {
			ConsumerRecords<String, String> records = consumer.poll(400);
			recordIterator = records.iterator();
		}

		if (recordIterator != null && recordIterator.hasNext()) {
			ConsumerRecord<String, String> record = recordIterator.next();
			Message msg = new Message();
			msg.setBody(record.value());
			if (!pa.containsCommandArgument("p")) { // exclude properties or not
				msg.putProperty("checksum", record.checksum());
				msg.putProperty("key", record.key());
				msg.putProperty("offset", record.offset());
				msg.putProperty("partition", record.partition());
				if (record.timestampType() != null && !record.timestampType().equals(TimestampType.NO_TIMESTAMP_TYPE)) {
					msg.putProperty("timestamp", Instants.from(Instants.instant(record.timestamp())).toStringUtc());
					msg.putProperty("timestamp_type", record.timestampType());
				}
			}
			result = msg;
			//xxx("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
		}

		//throw new CommandException("Failed creating message", ex);
		return result;
	}
	// http://stackoverflow.com/questions/28561147/how-to-read-data-using-kafka-consumer-api-from-beginning


	@Override
	protected boolean isSystemHeader(String headerName) {
		return true;
	}


	@Override
	protected void close() {
		if (consumer != null) {
			consumer.close();
		}
	}

}
