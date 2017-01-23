package de.galan.plunger.command.kafka;

import static de.galan.commons.util.Sugar.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.google.common.base.StandardSystemProperty;

import de.galan.commons.time.Durations;
import de.galan.commons.time.Instants;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCatCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Retrieves messages from a Kafka broker.
 */
public class KafkaCatCommand extends AbstractCatCommand {

	//parameter: timeout
	//parameter: key?
	//parameter: groupId
	//parameter: autoOffsetReset

	// Limitations:
	//- only String serializer/deserializer supported
	//- only one bootstrap server

	private KafkaConsumer<String, String> consumer;
	private Iterator<ConsumerRecord<String, String>> recordIterator;
	int timeout = 400;
	String groupId;
	String autoOffsetReset;
	private String clientId;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);
		clientId = "plunger-" + StandardSystemProperty.USER_NAME.value() + "-" + System.currentTimeMillis();
		groupId = optional(pa.getTarget().getParameterValue("groupId")).orElse("plunger"); //.orElse("plunger-" + UUID.randomUUID().toString());
		autoOffsetReset = optional(pa.getTarget().getParameterValue("autoOffsetReset")).orElse("earliest");
	}


	@Override
	protected void beforeFirstMessage(PlungerArguments pa) throws CommandException {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, pa.getTarget().getHost() + ":" + pa.getTarget().getPort());
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		//props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
		props.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG, "true");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(pa.getTarget().getDestination()));
		String timeoutDuration = pa.getTarget().getParameterValue("timeout");
		if (isNotBlank(timeoutDuration)) {
			timeout = Durations.dehumanize(timeoutDuration).intValue();
		}
	}


	@Override
	protected Message getNextMessage(PlungerArguments pa) throws CommandException {
		boolean commit = pa.containsCommandArgument("r");
		Message result = null;
		if (recordIterator == null || !recordIterator.hasNext()) {
			ConsumerRecords<String, String> records = consumer.poll(timeout);
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
		}
		if (commit && recordIterator != null && result != null) {
			consumer.commitSync();
		}

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