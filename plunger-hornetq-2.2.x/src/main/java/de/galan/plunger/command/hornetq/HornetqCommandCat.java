package de.galan.plunger.command.hornetq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.TextMessage;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;
import org.fusesource.jansi.Ansi.Color;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Escape;
import de.galan.plunger.util.MessageMarshaller;
import de.galan.plunger.util.Output;


/**
 * Lists messages in a destination
 * 
 * @author daniel
 */
public class HornetqCommandCat extends AbstractHornetqJmsCommand {

	@Override
	public void process(PlungerArguments jca) throws CommandException {
		if (isQueue()) {
			processQueue(jca);
		}
		else if (isTopic()) {
			processTopic(jca);
		}
	}


	private void processQueue(PlungerArguments jca) throws CommandException {
		try {
			getConnection().start();
			boolean browseOnly = !jca.hasCommandArgument("r");
			boolean firstMessage = true;
			MutableInt counter = new MutableInt();
			Integer limit = jca.getCommandArgumentPrefixInteger("n");
			if (browseOnly) {
				QueueBrowser browser = getSession().createBrowser((Queue)getDestination(), jca.getSelector());
				@SuppressWarnings("unchecked")
				Enumeration<TextMessage> enumeration = browser.getEnumeration();
				while(!isLimitExceeded(limit, counter) && enumeration.hasMoreElements()) {
					firstMessage = printSeparator(firstMessage, jca);
					TextMessage tm = enumeration.nextElement();
					Message message = constructMessage(tm, jca);
					printMessage(jca, message);
				}
			}
			else {
				MessageConsumer consumer = getSession().createConsumer(getDestination(), jca.getSelector());
				javax.jms.Message jmsMessage = null;
				while(!isLimitExceeded(limit, counter) && (jmsMessage = consumer.receiveNoWait()) != null) {
					firstMessage = printSeparator(firstMessage, jca);
					TextMessage tm = (TextMessage)jmsMessage;
					Message message = constructMessage(tm, jca);
					printMessage(jca, message);
				}
			}
		}
		catch (Exception ex) {
			throw new CommandException("Failed command ls", ex);
		}
	}


	private boolean isLimitExceeded(Integer limit, MutableInt counter) {
		boolean result = false;
		if (limit != null) {
			if (counter.intValue() >= limit) {
				result = true;
			}
			counter.increment();
		}
		return result;
	}


	private boolean printSeparator(boolean firstMessage, PlungerArguments jca) {
		if (!firstMessage) {
			if (!jca.hasCommandArgument("ee")) {
				Output.println(StringUtils.repeat("-", 64));
			}
		}
		return false;
	}


	private void processTopic(PlungerArguments jca) throws CommandException {
		try {
			getConnection().start();
			boolean firstMessage = true;
			MutableInt counter = new MutableInt();
			Integer limit = jca.getCommandArgumentPrefixInteger("n");
			//TopicSubscriber subscriber = getSession().createDurableSubscriber((Topic)getDestination(), "name", jca.getSelector(), true);
			MessageConsumer consumer = getSession().createConsumer(getDestination(), jca.getSelector());
			javax.jms.Message jmsMessage = null;
			while((jmsMessage = consumer.receive()) != null && !isLimitExceeded(limit, counter)) {
				firstMessage = printSeparator(firstMessage, jca);
				TextMessage tm = (TextMessage)jmsMessage;
				Message message = constructMessage(tm, jca);
				printMessage(jca, message);
			}
		}
		catch (Exception ex) {
			throw new CommandException("Failed command ls", ex);
		}
	}


	protected Message constructMessage(TextMessage tm, PlungerArguments jca) throws CommandException {
		try {
			Message result = new Message();
			// body
			String body = tm.getText();
			if (!jca.hasCommandArgument("b")) { // exclude body or not
				if (jca.hasCommandArgument("e") && !jca.hasCommandArgument("ee")) { // escape body (check against isEscape, otherwise twice escaped)
					body = new Escape().escape(body);
				}
				Integer cut = jca.getCommandArgumentPrefixInteger("c"); // limiting the body output
				if (cut != null) {
					boolean addDots = StringUtils.length(body) > cut;
					body = StringUtils.substring(body, 0, cut) + (addDots ? "..." : "");
				}
				result.setBody(body);
			}
			if (!jca.hasCommandArgument("p")) { // exclude properties or not
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
				result.putProperty("JMSReplyTo", "" + tm.getJMSReplyTo());
				result.putProperty("JMSTimestamp", tm.getJMSTimestamp() + " (" + new Date(tm.getJMSTimestamp()) + ")"); // TODO abstract and format
				result.putProperty("JMSType", tm.getJMSType());
			}

			return result;
		}
		catch (JMSException jex) {
			throw new CommandException("Could not read message", jex);
		}
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
