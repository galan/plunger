package de.galan.plunger.command.jms;

import static org.apache.commons.lang.StringUtils.*;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.commons.lang.StringUtils;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractPutCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Puts messages on a JMS message-server.
 * 
 * @author daniel
 */
public class AbstractJmsPutCommand extends AbstractPutCommand {

	AbstractJms jms;
	MessageProducer producer;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		if (!pa.getTarget().hasDestination()) {
			throw new CommandException("No destination has been specified");
		}
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

			// TODO ttl
			//producer.send(textMessge, producer.getDeliveryMode(), producer.getPriority(), 120000);
			producer.send(textMessge);
		}
		catch (Exception ex) {
			//TODO option to skip failed lines >> abstract class Output.error("Skipping failed line " + lineCount + ": " + line);
			throw new CommandException("", ex);
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
