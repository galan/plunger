package de.galan.plunger.command.activemq;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCountCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class AmqCountCommand extends AbstractCountCommand {

	private MBeanServerConnection conn;


	@Override
	protected long getCount(PlungerArguments pa) throws CommandException {
		long result = 0;
		try {
			//ObjectName activeMQ = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Broker,Destination=testing");
			ObjectName activeMQ = new ObjectName("org.apache.activemq:BrokerName=localhost,Type=Broker");
			BrokerViewMBean mbean = MBeanServerInvocationHandler.newProxyInstance(conn, activeMQ, BrokerViewMBean.class, true);
			for (ObjectName name: mbean.getQueues()) {
				QueueViewMBean queueMbean = MBeanServerInvocationHandler.newProxyInstance(conn, name, QueueViewMBean.class, true);

				if (queueMbean.getName().equals(pa.getDestination())) {
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
		return result;
	}


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		try {
			JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
			JMXConnector jmxc = JMXConnectorFactory.connect(url);
			conn = jmxc.getMBeanServerConnection();
		}
		catch (Exception ex) {
			throw new CommandException("irks", ex);
		}
	}


	@Override
	protected void close() {
		//conn.
	}

}
