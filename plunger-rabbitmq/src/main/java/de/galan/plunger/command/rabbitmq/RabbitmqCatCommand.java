package de.galan.plunger.command.rabbitmq;

import java.io.IOException;
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
		try {
			Connection connection = core.getConnection();
			channel = connection.createChannel();

			// TODO not supported by rabbitmq
			// maybe https://www.rabbitmq.com/consumer-cancel.html
			//boolean browseOnly = !pa.containsCommandArgument("r");
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
				for (Entry<String, Object> entry: response.getProps().getHeaders().entrySet()) {
					result.putProperty(entry.getKey(), entry.getValue());
				}
			}
			/*
			try {
				result = new Message();
				result.setBody(tm.getText());
				// body
				if (!pa.containsCommandArgument("p")) { // exclude properties or not
					@SuppressWarnings("unchecked")
					Enumeration<String> enumKeys = tm.getPropertyNames();
					while(enumKeys.hasMoreElements()) {
						String key = enumKeys.nextElement();
						result.putProperty(key, tm.getObjectProperty(key));
					}

					result.putProperty("JMSCorrelationID", tm.getJMSCorrelationID());
					result.putProperty("JMSDeliveryMode", tm.getJMSDeliveryMode());
					result.putProperty("JMSDestination", "" + tm.getJMSDestination());
					if (tm.getJMSExpiration() != 0) {
						//TODO implement expiriation in put first
						result.putPropertyTimestamp("JMSExpiration", tm.getJMSExpiration()); //TODO format and write additional in humantime "in 12m10s"
					}
					result.putProperty("JMSMessageID", tm.getJMSMessageID());
					result.putProperty("JMSPriority", tm.getJMSPriority());
					result.putProperty("JMSRedelivered", tm.getJMSRedelivered());
					result.putProperty("JMSReplyTo", tm.getJMSReplyTo() == null ? "" : "" + tm.getJMSReplyTo());
					result.putPropertyTimestamp("JMSTimestamp", tm.getJMSTimestamp());
					result.putProperty("JMSType", tm.getJMSType());
				}f
			}
			catch (JMSException jex) {
				throw new CommandException("Could not read message", jex);
			}
			 */
		}
		return result;
	}


	@Override
	protected boolean isSystemHeader(String headerName) {
		return false;
	}


	@Override
	protected void close() {
		core.close();
	}

	/*
	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		RabbitmqUtil util = new RabbitmqUtil();
		setProviderInformation(util);
		setJms(new RabbitmqJms(util.getTransportConfiguration(pa)));
		super.initialize(pa);
	}
	 */

}
