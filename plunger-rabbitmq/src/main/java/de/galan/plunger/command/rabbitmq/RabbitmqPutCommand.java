package de.galan.plunger.command.rabbitmq;

import static com.google.common.base.Charsets.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;

import com.google.common.collect.Maps;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractPutCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Writes messages to a destination on a HornetQ messaging server.
 *
 * @author daniel
 */
public class RabbitmqPutCommand extends AbstractPutCommand {

	private final static int PERSISTENT_DELIVERY_MODE = com.rabbitmq.client.MessageProperties.PERSISTENT_BASIC.getDeliveryMode(); //2

	private RabbitmqCore core;
	private Channel channel;


	@Override
	protected void sendMessage(PlungerArguments pa, Message message, long count) throws CommandException {
		core = new RabbitmqCore();
		core.initialize(pa);
		try {
			channel = core.getConnection().createChannel();
		}
		catch (IOException ex) {
			throw new CommandException("Failed creating channel", ex);
		}

		byte[] bodyBytes = defaultString(message.getBody()).getBytes(UTF_8);
		BasicProperties basic = mapProperties(message);
		//TODO routingkey parameter
		try {
			channel.basicPublish(pa.getTarget().getDestination(), EMPTY, false, basic, bodyBytes);
		}
		catch (IOException ex) {
			throw new CommandException("Failed sending message", ex);
		}

		/*
		Long ttl = producer.getTimeToLive();
		if (pa.containsCommandArgument("t")) {
			ttl = Durations.dehumanize(pa.getCommandArgument("t"));
		}
		Integer prio = producer.getPriority();
		if (pa.containsCommandArgument("p")) {
			prio = pa.getCommandArgumentLong("p").intValue();
		}
		 */
	}


	protected BasicProperties mapProperties(Message message) {
		Builder builder = new BasicProperties.Builder().deliveryMode(PERSISTENT_DELIVERY_MODE);
		if (message != null && message.getProperties() != null && !message.getProperties().isEmpty()) {
			builder.headers(Maps.newHashMap(message.getProperties()));
		}
		/*
		if (priority != null) {
			// https://www.rabbitmq.com/priority.html
			builder.priority(priority);
		}
		if (ttl != null) {
			// https://www.rabbitmq.com/ttl.html
			builder.expiration(Long.toString(ttl));
		}
		 */
		return builder.build();
	}


	@Override
	protected void close() {
		core.close();
	}

}
