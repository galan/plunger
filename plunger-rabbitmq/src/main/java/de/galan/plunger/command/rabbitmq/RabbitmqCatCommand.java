package de.galan.plunger.command.rabbitmq;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Charsets;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCatCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Prints messags from a HornetQ messaging server.
 *
 * @author daniel
 */
public class RabbitmqCatCommand extends AbstractCatCommand {

	private RabbitmqCore core;
	private Channel channel;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);
		core = new RabbitmqCore();
		core.initialize(pa);
	}


	@Override
	protected void beforeFirstMessage(PlungerArguments pa) throws CommandException {
		// TODO not supported by rabbitmq, maybe https://www.rabbitmq.com/consumer-cancel.html
		boolean browseOnly = !pa.containsCommandArgument("r");
		if (browseOnly) {
			throw new CommandException("Only removing reading is supported with RabbitMQ (use -r switch).");
		}

		try {
			Connection connection = core.getConnection();
			channel = connection.createChannel();

		}
		catch (IOException ex) {
			throw new CommandException("Failed creating channel", ex);
		}
	}


	@Override
	protected Message getNextMessage(PlungerArguments pa) throws CommandException {
		Message result = null;
		try {
			GetResponse response = channel.basicGet(pa.getTarget().getDestination(), true);
			result = constructMessage(response, pa);
		}
		catch (IOException ex) {
			throw new CommandException("Failed creating message", ex);
		}
		return result;
	}


	protected Message constructMessage(GetResponse response, PlungerArguments pa) {
		Message result = null;
		if (response != null) {
			result = new Message();
			//AMQP.BasicProperties props = response.getProps();
			//long deliveryTag = response.getEnvelope().getDeliveryTag();
			String body = new String(response.getBody(), Charsets.UTF_8);
			result.setBody(body);
			if (!pa.containsCommandArgument("p")) { // exclude properties or not
				Map<String, Object> headers = (response.getProps().getHeaders() == null) ? new HashMap<>() : response.getProps().getHeaders();
				for (Entry<String, Object> entry: headers.entrySet()) {
					result.putProperty(entry.getKey(), entry.getValue());
				}
				result.putProperty("rmq.exchange", response.getEnvelope().getExchange());
				result.putProperty("rmq.redeliver", response.getEnvelope().isRedeliver());
				result.putProperty("rmq.routingkey", trimToNull(response.getEnvelope().getRoutingKey()));
				result.putProperty("rmq.contenttype", response.getProps().getContentType());
				result.putProperty("rmq.priority", response.getProps().getPriority());
				result.putProperty("rmq.expiration", response.getProps().getExpiration());
				result.putProperty("rmq.timestamp", response.getProps().getTimestamp());
			}
		}
		return result;
	}


	@Override
	protected boolean isSystemHeader(String headerName) {
		return startsWith(headerName, "rmq.");
	}


	@Override
	protected void close() {
		core.close();
	}

}
