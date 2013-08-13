package de.galan.plunger.command.activemq;

import static org.apache.commons.lang.StringUtils.*;

import java.io.IOException;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCountCommand;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.domain.Target;
import de.galan.plunger.util.Output;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class AmqCountCommand extends AbstractCountCommand {

	private JMXConnector connector;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		try {
			Target target = pa.getTarget();
			String port = defaultIfBlank(target.getParameterValue("jmxPort"), "1099");
			String url = "service:jmx:rmi:///jndi/rmi://" + target.getHost() + ":" + port + "/jmxrmi";
			connector = JMXConnectorFactory.connect(new JMXServiceURL(url));
		}
		catch (IOException ex) {
			throw new CommandException("...", ex);
		}
	}


	@Override
	protected long getCount(PlungerArguments pa) throws CommandException {
		long result = 0;
		/*
		try {
			ObjectName activeMQ = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Broker");
			BrokerViewMBean mbean = MBeanServerInvocationHandler.newProxyInstance(conn, activeMQ, BrokerViewMBean.class, true);
			for (ObjectName name: mbean.getQueues()) {
				QueueViewMBean queueMbean = MBeanServerInvocationHandler.newProxyInstance(conn, name, QueueViewMBean.class, true);

				if (queueMbean.getName().equals(pa.getTarget().getDestination())) {
					//queueMbean.getConsumerCount()
					result = queueMbean.getQueueSize();
					//queueViewBeanCache.put(cacheKey, queueMbean);
					//return queueMbean;
				}
			}
			
			//org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=bla
			//org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Topic,destinationName=something
			connector.connect();
			MBeanServerConnection connection = connector.getMBeanServerConnection();

			ObjectName nameList = new ObjectName("org.apache.activemq:brokerName=localhost,type=Broker");
			BrokerViewMBean mbList = MBeanServerInvocationHandler.newProxyInstance(connection, nameList, BrokerViewMBean.class, true);

			for (JmxDestination jd: new JmxDestinations(mbList)) {
				ObjectName nameConsumers = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=" + jd.getDestinationType()
						+ ",destinationName=" + jd.getObjectName());
				DestinationViewMBean mbView = MBeanServerInvocationHandler.newProxyInstance(connection, nameConsumers, DestinationViewMBean.class, true);
				if (!startsWith(jd.getObjectName(), "ActiveMQ.Advisory.")) {
					printDestination(pa, jd.getDisplayName(), mbView.getConsumerCount(), mbView.getQueueSize(), !jd.isTemporary());
				}
			}
		}
		catch (Exception ex) {
			throw new CommandException("to do....", ex);
		}

		
		try {
			//ObjectName activeMQ = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Broker,Destination=testing");
			ObjectName activeMQ = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Broker");
			BrokerViewMBean mbean = MBeanServerInvocationHandler.newProxyInstance(conn, activeMQ, BrokerViewMBean.class, true);
			for (ObjectName name: mbean.getQueues()) {
				QueueViewMBean queueMbean = MBeanServerInvocationHandler.newProxyInstance(conn, name, QueueViewMBean.class, true);

				if (queueMbean.getName().equals(pa.getTarget().getDestination())) {
					//queueMbean.getConsumerCount()
					result = queueMbean.getQueueSize();
					//queueViewBeanCache.put(cacheKey, queueMbean);
					//return queueMbean;
				}
			}
		}
		catch (Exception ex) {
			throw new CommandException("uff", ex);
		}
		*/
		return result;
	}


	@Override
	protected void close() {
		if (connector != null) {
			try {
				connector.close();
			}
			catch (IOException ex) {
				Output.error("xx");
			}
		}
	}

}
