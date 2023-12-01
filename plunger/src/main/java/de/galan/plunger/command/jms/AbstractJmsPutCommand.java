package de.galan.plunger.command.jms;

import static org.apache.commons.lang3.StringUtils.*;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;

import de.galan.commons.time.Durations;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractPutCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Puts messages on a JMS message-server.
 */
public class AbstractJmsPutCommand extends AbstractPutCommand {

	AbstractJms jms;
	MessageProducer producer;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);
		jms.initialize(pa);
		try {
			producer = jms.getSession().createProducer(jms.getDestination());
		}
		catch (JMSException ex) {
			throw new CommandException("Producer could not be created", ex);
		}
	}


	public AbstractJms getJms() {
		return jms;
	}


	public void setJms(AbstractJms jms) {
		this.jms = jms;
	}


	@Override
	protected void sendMessage(PlungerArguments pa, Message message, long count) throws CommandException {
		try {
			TextMessage textMessge = jms.getSession().createTextMessage(message.getBody());

			// map properties
			for (String key: message.getProperties().keySet()) {
				if (!startsWith(key, "JMS") || StringUtils.equals(key, "JMSXGroupID")) {
					textMessge.setObjectProperty(key, message.getProperty(key));
				}
			}

			Long ttl = producer.getTimeToLive();
			if (pa.containsCommandArgument("t")) {
				ttl = Durations.dehumanize(pa.getCommandArgument("t"));
			}
			Integer prio = producer.getPriority();
			if (pa.containsCommandArgument("p")) {
				prio = pa.getCommandArgumentLong("p").intValue();
			}
			producer.send(textMessge, producer.getDeliveryMode(), prio, ttl);
		}
		catch (Exception ex) {
			//TODO option to skip failed lines >> abstract class Output.error("Skipping failed line " + lineCount + ": " + line);
			throw new CommandException("Failed sending message", ex);
		}
	}


	@Override
	protected void close() {
		try {
			producer.close();
		}
		catch (JMSException ex) {
			Output.error("Failed to close producer: " + ex.getMessage());
		}
		jms.close();
	}

}
