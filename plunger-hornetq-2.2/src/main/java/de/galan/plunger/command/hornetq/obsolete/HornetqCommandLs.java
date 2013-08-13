package de.galan.plunger.command.hornetq.obsolete;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.fusesource.jansi.Ansi.Color;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientRequestor;
import org.hornetq.api.core.client.ClientSession.QueueQuery;
import org.hornetq.api.core.management.ManagementHelper;
import org.hornetq.api.core.management.ResourceNames;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;
import de.galan.plunger.util.StringCaseInsensitiveComparator;


/**
 * Lists all destinations and their consumer/message count.<br/>
 * See also for JMS approach:
 * https://github.com/hornetq/hornetq/blob/master/examples/jms/management/src/main/java/org/hornetq/jms/example/
 * ManagementExample.java
 * 
 * @author daniel
 */
public class HornetqCommandLs extends AbstractHornetqCoreCommand {

	@Override
	public void process(PlungerArguments pa) throws CommandException {
		try {
			ClientRequestor requestor = new ClientRequestor(getSession(), "jms.queue.hornetq.management");
			ClientMessage message = getSession().createMessage(false);
			ManagementHelper.putAttribute(message, ResourceNames.CORE_SERVER, "addressNames");
			ClientMessage reply = requestor.request(message);
			Object[] arrayObjects = (Object[])ManagementHelper.getResult(reply);
			String[] array = Arrays.copyOf(arrayObjects, arrayObjects.length, String[].class);
			Arrays.sort(array, new StringCaseInsensitiveComparator());
			boolean filterTemp = pa.containsCommandArgument("t");
			boolean filterPersistent = pa.containsCommandArgument("p");
			for (String address: array) {
				QueueQuery queueQuery = getSession().queueQuery(SimpleString.toSimpleString(address));
				boolean persistent = queueQuery.isDurable();
				boolean filterPassed = (persistent && !filterPersistent) || (!persistent && !filterTemp);
				boolean optionMessages = !(pa.containsCommandArgument("m") && (queueQuery.getMessageCount() <= 0));
				boolean optionConsumer = !(pa.containsCommandArgument("c") && (queueQuery.getConsumerCount() <= 0));
				if (filterPassed && optionMessages && optionConsumer) {
					Color destinationColor = StringUtils.startsWith(address, "queue.") ? Color.CYAN : Color.GREEN;
					Output.print(destinationColor, address);
					if (!pa.containsCommandArgument("i")) {
						if (!queueQuery.isDurable()) {
							Output.print(" (temporary)");
						}
						Output.print(" (");
						Output.print(Color.BLUE, "" + queueQuery.getConsumerCount());
						Output.print("/");
						Output.print(Color.MAGENTA, "" + queueQuery.getMessageCount());
						Output.print(")");
					}
					Output.println("");
				}
			}
			//requestor.close();
		}
		catch (Exception ex) {
			throw new CommandException("Could not retrieve destinations", ex);
		}

		/*
		ClientRequestor requestor = new ClientRequestor(session, "jms.queue.hornetq.management");
		ClientMessage message = session.createMessage(false);
		//ManagementHelper.putAttribute(message, pa.getDestination(), "messageCount");
		ManagementHelper.putAttribute(message, ResourceNames.CORE_SERVER, "addressNames");
		ClientMessage reply = requestor.request(message);
		int count = (Integer)ManagementHelper.getResult(reply);
		Output.println("There are " + count + " messages in exampleQueue");
		*/

		/*
		//ObjectName objectName = new ObjectName("org.hornetq:module=JMS,type=Topic,name=marketMakerTopic");
		//int subscribersCounter = (Integer)server.getAttribute(objectName, new String("listDurableSubscriptions"));
		//Output.println("Utils.getNumOfSubscribersFromTopic(),Current subscribers number=" + subscribersCounter);

		try {
			String jmxUrl = "service:jmx:rmi:///jndi/rmi://localhost:3000/jmxrmi";
			ObjectName on = ObjectNameBuilder.DEFAULT.getHornetQServerObjectName();
			JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(jmxUrl), new HashMap<String, String>());
			MBeanServerConnection mbsc = connector.getMBeanServerConnection();
			HornetQServerControl serverControl = MBeanServerInvocationHandler.newProxyInstance(mbsc, on, HornetQServerControl.class, false);
			serverControl.getAddressNames();
		}
		catch (Exception ex) {
			Output.error(ExceptionUtils.getFullStackTrace(ex));
		}
		*/
	}

}
