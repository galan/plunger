package de.galan.plunger.command.hornetq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.fusesource.jansi.Ansi.Color;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.utils.BufferHelper;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Escape;
import de.galan.plunger.util.MessageMarshaller;
import de.galan.plunger.util.Output;


/**
 * Lists messages in a destination via Core API.
 * 
 * @author daniel
 * @deprecated
 */
@Deprecated
public class HornetqCommandCatCore extends AbstractHornetqCoreCommand {

	@Override
	public void process(PlungerArguments jca) throws CommandException {
		try {
			boolean browseOnly = !jca.hasCommandArgument("r");
			ClientConsumer consumer = getSession().createConsumer(jca.getDestination(), jca.getSelector(), browseOnly);
			ClientMessage cm = getNextMessage(consumer);
			Message message = null;
			while(cm != null) {
				message = constructMessage(cm, jca);
				printMessage(jca, message);
				cm.acknowledge();

				cm = getNextMessage(consumer);
				if (cm != null && !jca.hasCommandArgument("ee")) {
					Output.println(StringUtils.repeat("-", 64));
				}
			}

			consumer.close();
		}
		catch (Exception ex) {
			throw new CommandException("Failed command ls", ex);
		}
	}


	protected ClientMessage getNextMessage(ClientConsumer consumer) throws HornetQException {
		return consumer.receiveImmediate();
	}


	protected Message constructMessage(ClientMessage cm, PlungerArguments jca) {
		Message result = new Message();

		// body
		String body = BufferHelper.readNullableSimpleStringAsString(cm.getBodyBuffer());
		if (!jca.hasCommandArgument("b")) { // exclude body or not
			if (jca.hasCommandArgument("e") && !jca.hasCommandArgument("ee")) { // escape body (check against isEscape, otherwise twice escaped)
				body = new Escape().escape(body);
			}
			String cut = jca.getCommandArgumentMatching("c[0-9]+"); // limiting the body output
			if (StringUtils.isNotBlank(cut)) {
				int limit = Integer.valueOf(StringUtils.substring(cut, 1, cut.length()));
				boolean addDots = StringUtils.length(body) > limit;
				body = StringUtils.substring(body, 0, limit) + (addDots ? "..." : "");
			}
			result.setBody(body);
		}
		if (!jca.hasCommandArgument("p")) { // exclude properties or not
			for (SimpleString key: cm.getPropertyNames()) {
				Object value = cm.getObjectProperty(key);
				if (value instanceof SimpleString) {
					value = value.toString();
				}
				result.putProperty(key.toString(), value);
			}
			// Missing: JMSDeliveryMode, JMSCorrelationID
			result.putProperty("JMSMessageID", cm.getUserID());
			result.putProperty("HQClass", cm.getClass());
			result.putProperty("JMSXDeliveryCount", cm.getDeliveryCount());
			result.putProperty("HQExpiration", cm.getExpiration());
			result.putProperty("JMSPriority", cm.getPriority());
			result.putProperty("JMSTimestamp", cm.getTimestamp());
			result.putProperty("JMSType", cm.getType());

		}

		return result;
	}


	private void printMessage(PlungerArguments jca, Message message) {
		if (jca.hasCommandArgument("ee")) {
			String[] marshalled = new MessageMarshaller().marshalParts(message);
			Output.print(Color.GREEN, marshalled[0]);
			Output.print(marshalled[1]);
			Output.println(Color.YELLOW, marshalled[2]);
		}
		else {
			List<String> keys = new ArrayList<>(message.getProperties().keySet());
			Collections.sort(keys);
			for (String key: keys) {
				if (StringUtils.startsWith(key, "JMS") || StringUtils.startsWith(key, "HQ")) {
					Output.print(Color.BLUE, key + ":");
				}
				else {
					Output.print(Color.GREEN, key + ":");
				}
				Output.println(" " + message.getProperty(key));
			}
			if (StringUtils.isNotBlank(message.getBody())) {
				Output.println(Color.YELLOW, message.getBody());
			}
		}
	}

}
