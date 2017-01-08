package de.galan.plunger.command.kafka;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractPutCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Writes messages to a destination on a HornetQ messaging server.
 *
 * @author daniel
 */
public class KafkaPutCommand extends AbstractPutCommand {

	//private final static int PERSISTENT_DELIVERY_MODE = com.rabbitmq.client.MessageProperties.PERSISTENT_BASIC.getDeliveryMode(); //2

	private Producer<String, String> producer;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);
		/*
		core = new RabbitmqCore();
		core.initialize(pa);
		try {
			channel = core.getConnection().createChannel();
		}
		catch (IOException ex) {
			throw new CommandException("Failed creating channel", ex);
		}
		*/
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, pa.getTarget().getHost() + ":" + pa.getTarget().getPort());
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		producer = new KafkaProducer<>(props);

	}


	@Override
	protected void sendMessage(PlungerArguments pa, Message message, long count) throws CommandException {
		producer.send(new ProducerRecord<String, String>(pa.getTarget().getDestination(), message.getBody()));
	}


	@Override
	protected void close() {
		if (producer != null) {
			producer.close();
		}
	}

}
