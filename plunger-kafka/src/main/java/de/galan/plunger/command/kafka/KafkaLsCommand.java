package de.galan.plunger.command.kafka;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractLsCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Lists all destinations on a HornetQ messaging server.
 */
public class KafkaLsCommand extends AbstractLsCommand {

	private KafkaConsumer<String, String> consumer;

	ObjectMapper mapper;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		mapper = new ObjectMapper();
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
	}


	@Override
	protected void process(PlungerArguments pa) throws CommandException {

		Map<String, List<PartitionInfo>> topics = consumer.listTopics();
		for (String topic: topics.keySet()) {
			if (StringUtils.equals("__consumer_offsets", topic)) {
				continue;
			}
			List<PartitionInfo> infos = topics.get(topic);
			//(topic = tt01, partition = 0, leader = 1, replicas = [1,], isr = [1,])]
			/*
			for (PartitionInfo info: infos) {
				//Output.println("infos: " + info.replicas()[0].);
				info.partition();
			}
			*/
			printDestination(pa, topic, 0, infos.size(), false);
		}
	}


	@Override
	protected void close() {
		if (consumer != null) {
			consumer.close();
		}
	}

}
