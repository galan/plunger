package de.galan.plunger.command.hornetq2;

import java.util.Date;
import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.TextMessage;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCatCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class HornetqCatCommand extends AbstractCatCommand {

	HornetqUtil util;
	HornetqJms jms;

	MessageConsumer consumer;
	Enumeration<TextMessage> enumeration;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		util = new HornetqUtil();
		jms = new HornetqJms();
		jms.initialize(pa, util.getTransportConfiguration(pa));
	}


	@Override
	protected void beforeFirstMessage(PlungerArguments pa) throws CommandException {
		try {
			jms.getConnection().start();
			boolean browseOnly = !pa.containsCommandArgument("r");

			if (jms.isQueue() && browseOnly) {
				QueueBrowser browser = jms.getSession().createBrowser((Queue)jms.getDestination(), pa.getCommandArgument("s"));
				@SuppressWarnings("unchecked")
				Enumeration<TextMessage> xxx = browser.getEnumeration();
				enumeration = xxx;
			}
			else {
				consumer = jms.getSession().createConsumer(jms.getDestination(), pa.getCommandArgument("s"));
			}
		}
		catch (JMSException ex) {
			throw new CommandException("Failed to start connection", ex);
		}
	}


	@Override
	protected Message getNextMessage(PlungerArguments pa) throws CommandException {
		Message result = null;
		try {
			boolean browseOnly = !pa.containsCommandArgument("r");
			if (jms.isQueue()) {
				if (browseOnly) {
					if (enumeration.hasMoreElements()) {
						result = constructMessage(enumeration.nextElement(), pa);
					}
				}
				else {
					result = constructMessage((TextMessage)consumer.receiveNoWait(), pa);
				}
			}
			else if (jms.isTopic()) {
				result = constructMessage((TextMessage)consumer.receive(), pa);
			}
		}
		catch (JMSException jex) {
			throw new CommandException("Failed to retrieve next message", jex);
		}

		return result;
	}


	protected Message constructMessage(TextMessage tm, PlungerArguments pa) throws CommandException {
		try {
			Message result = new Message();
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
					result.putProperty("JMSExpiration", tm.getJMSExpiration() + " (" + new Date(tm.getJMSExpiration()) + ")"); //TODO format and write additional in humantime "in 12m10s"
				}
				result.putProperty("JMSMessageID", tm.getJMSMessageID());
				result.putProperty("JMSPriority", tm.getJMSPriority());
				result.putProperty("JMSRedelivered", tm.getJMSRedelivered());
				result.putProperty("JMSReplyTo", tm.getJMSReplyTo() == null ? "" : "" + tm.getJMSReplyTo());
				result.putProperty("JMSTimestamp", tm.getJMSTimestamp() + " (" + new Date(tm.getJMSTimestamp()) + ")"); // TODO abstract and format
				result.putProperty("JMSType", tm.getJMSType());
			}

			return result;
		}
		catch (JMSException jex) {
			throw new CommandException("Could not read message", jex);
		}
	}


	@Override
	protected void close() {
		jms.close();
	}


	@Override
	protected boolean isSystemHeader(String headerName) {
		return util.isSystemHeader(headerName);
	}

}
