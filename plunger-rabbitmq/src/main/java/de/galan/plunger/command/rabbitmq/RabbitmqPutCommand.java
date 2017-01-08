package de.galan.plunger.command.rabbitmq;

import static com.google.common.base.Charsets.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;

import de.galan.commons.time.Durations;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractPutCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Writes messages to a destination on a RabbitMQ messaging server.
 */
public class RabbitmqPutCommand extends AbstractPutCommand {

	private final static int PERSISTENT_DELIVERY_MODE = com.rabbitmq.client.MessageProperties.PERSISTENT_BASIC.getDeliveryMode(); //2

	private RabbitmqCore core;
	private Channel channel;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);
		core = new RabbitmqCore();
		core.initialize(pa);
		try {
			channel = core.getConnection().createChannel();
		}
		catch (IOException ex) {
			throw new CommandException("Failed creating channel", ex);
		}
	}


	@Override
	protected void sendMessage(PlungerArguments pa, Message message, long count) throws CommandException {
		byte[] bodyBytes = defaultString(message.getBody()).getBytes(UTF_8);
		BasicProperties basic = mapProperties(pa, message);
		String routingkey = defaultString(pa.getCommandArgument("r"));
		try {
			channel.basicPublish(pa.getTarget().getDestination(), routingkey, false, basic, bodyBytes);
		}
		catch (IOException ex) {
			throw new CommandException("Failed sending message", ex);
		}
	}


	protected BasicProperties mapProperties(PlungerArguments pa, Message message) {
		Builder builder = new BasicProperties.Builder().deliveryMode(PERSISTENT_DELIVERY_MODE);
		Map<String, Object> headers = new HashMap<>();
		Map<String, Object> properties = (message == null || message.getProperties() == null) ? ImmutableMap.of() : message.getProperties();

		// map properties
		for (Entry<String, Object> property: properties.entrySet()) {
			if (!property.getKey().startsWith("rmq.")) {
				headers.put(property.getKey(), property.getValue());
			}
		}

		// ttl
		if (pa.containsCommandArgument("t")) {
			// https://www.rabbitmq.com/ttl.html
			Long ttl = Durations.dehumanize(pa.getCommandArgument("t"));
			if (ttl != null) {
				builder.expiration(Long.toString(ttl));
			}
		}

		// priority
		if (pa.containsCommandArgument("p")) {
			// https://www.rabbitmq.com/priority.html
			Long prio = pa.getCommandArgumentLong("p");
			if (prio != null) {
				builder.priority(prio.intValue());
			}
		}

		builder.headers(headers);
		return builder.build();
	}


	@Override
	protected void close() {
		try {
			channel.close();
		}
		catch (IOException | TimeoutException ex) {
			Output.error("Failed to close channel: " + ex.getMessage());
		}
		core.close();
	}

}
