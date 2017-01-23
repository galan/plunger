package de.galan.plunger.command.kafka;

import static de.galan.commons.time.Instants.*;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringDeserializer;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractLsCommand;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Lists all destinations on a HornetQ messaging server.
 */
public class KafkaLsCommand extends AbstractLsCommand {

	private KafkaConsumer<String, String> consumer;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, pa.getTarget().getHost() + ":" + pa.getTarget().getPort());
		//props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		//props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		//props.put(ConsumerConfig.GROUP_ID_CONFIG, "plunger-" + UUID.randomUUID().toString());
		//props.put(ConsumerConfig.CLIENT_ID_CONFIG, "your_client_id");
		//props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG, "true");
		//props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 5000);
		//props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 10001);
		//props.put("controller.socket.timeout.ms", 10000);
		Output.println("consumer 1 " + from(now()).toStringUtc());
		consumer = new KafkaConsumer<>(props);
		Output.println("consumer 2 " + from(now()).toStringUtc());
	}


	@Override
	protected void process(PlungerArguments pa) throws CommandException {
		Output.println("consumer 3 " + from(now()).toStringUtc());
		Map<String, List<PartitionInfo>> topics = consumer.listTopics();
		Output.println("consumer 4" + from(now()).toStringUtc());
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
			printDestination(pa, topic, 0, infos.size(), true);
		}
	}


	@Override
	protected void close() {
		if (consumer != null) {
			consumer.close();
		}
	}

}
