package de.galan.plunger.command.hornetq;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.commons.lang.StringUtils;
import org.fusesource.jansi.Ansi.Color;

import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.MessageMarshaller;
import de.galan.plunger.util.MessageReader;
import de.galan.plunger.util.Output;
import de.galan.plunger.util.SystemMessageReader;


/**
 * Puts messages on a destination
 * 
 * @author daniel
 */
public class HornetqCommandPut extends AbstractHornetqJmsCommand {

	@Override
	public void process(PlungerArguments jca) {
		//MessageReader reader = new FileMessageReader("/home/daniel/workspace-4.2/plunger/src/test/resources/test2");
		MessageReader reader = new SystemMessageReader();

		MessageProducer producer = null;
		try {
			producer = getSession().createProducer(getDestination());

			int lineCount = 0;
			MessageMarshaller mm = new MessageMarshaller();
			String line = null;
			while((line = reader.read()) != null) {
				lineCount++;
				try {
					Message msg = mm.unmarshal(line);
					TextMessage textMessge = getSession().createTextMessage(msg.getBody());

					// map properties
					for (String key: msg.getProperties().keySet()) {
						if (!StringUtils.startsWith(key, "JMS") || StringUtils.equals(key, "JMSXGroupID")) {
							textMessge.setObjectProperty(key, msg.getProperty(key));
						}
					}
					if (jca.isVerbose()) {
						//TODO abstract from HornetQCommandLs
						String[] marshalled = new MessageMarshaller().marshalParts(msg);
						Output.print(Color.GREEN, marshalled[0]);
						Output.print(marshalled[1]);
						Output.println(Color.YELLOW, marshalled[2]);
					}

					// TODO ttl
					//producer.send(textMessge, producer.getDeliveryMode(), producer.getPriority(), 120000);
					producer.send(textMessge);
				}
				catch (Exception ex) {
					ex/**/./**/printStackTrace/**/();
					Output.error("Skipping failed line " + lineCount + ": " + line);
				}
			}

			/*
			//getSession().createQueue("jms.queue.test-plunger", "test-plunger");

			ClientProducer producer = getSession().createProducer(jca.getDestination());
			int lineCount = 0;
			MessageMarshaller mm = new MessageMarshaller();
			String line = null;
			while((line = reader.read()) != null) {
				lineCount++;
				try {
					Message msg = mm.unmarshal(line);

					//ClientMessage cm = new ClientMessageImpl();
					ClientMessage cm = getSession().createMessage(true);

					// map properties
					for (String key: msg.getProperties().keySet()) {
						cm.putObjectProperty(key, msg.getProperty(key));
					}

					cm.getBodyBuffer().writeString(msg.getBody());

					producer.send(cm);
					//getSession().start();

					getSession().commit();
				}
				catch (Exception ex) {
					Output.error("Skipping failed line " + lineCount + ": " + line);
				}
			}
			*/

		}
		catch (Exception ex) {
			Output.error("Failed executing command 'put': " + ex.getMessage());
		}
		if (producer != null) {
			try {
				producer.close();
			}
			catch (JMSException ex) {
				//TODO verbose
			}
		}
	}

}
