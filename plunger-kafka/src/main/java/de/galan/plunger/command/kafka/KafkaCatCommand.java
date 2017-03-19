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
import com.google.common.primitives.Longs;

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

	private KafkaConsumer<String, String> consumer;
	private Iterator<ConsumerRecord<String, String>> recordIterator;
	int timeout = 1000;
	String groupId;
	String autoOffsetReset;
	private String clientId;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);
		clientId = "plunger-" + StandardSystemProperty.USER_NAME.value() + "-" + System.currentTimeMillis();
		groupId = optional(pa.getTarget().getParameterValue("group"))
			.orElse(optional(pa.getTarget().getParameterValue("groupId"))
				.orElse("plunger-" + StandardSystemProperty.USER_NAME.value()));
		autoOffsetReset = optional(pa.getTarget().getParameterValue("autoOffsetReset")).orElse("earliest");
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
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "" + determineMaxPollRecords(pa));
		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(pa.getTarget().getDestination()));
		String timeoutDuration = pa.getTarget().getParameterValue("timeout");
		if (isNotBlank(timeoutDuration)) {
			timeout = Durations.dehumanize(timeoutDuration).intValue();
		}
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
					msg.putProperty("timestamp_type", record.timestampType().toString());
				}
			}
			result = msg;
		}
		if (commit && recordIterator != null && result != null) {
			consumer.commitSync();
		}

		return result;
	}


	@Override
	protected boolean isSystemHeader(String headerName) {
		// kafka does not provider own header information, only payload. Meta-data is provided as header instead.
		return true;
	}


	@Override
	protected void close() {
		if (consumer != null) {
			consumer.unsubscribe();
			consumer.close();
		}
	}

}
