package de.galan.plunger.command.activemq;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractLsCommand;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class AmqLsCommand extends AbstractLsCommand {

	private JMXConnector connector;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		try {
			connector = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"));
		}
		catch (IOException ex) {
			throw new CommandException("...", ex);
		}
	}


	@Override
	protected void process(PlungerArguments pa) throws CommandException {
		try {
			connector.connect();
			MBeanServerConnection connection = connector.getMBeanServerConnection();

			ObjectName mbeanName = new ObjectName("org.apache.activemq:brokerName=localhost,type=Broker");
			BrokerViewMBean mbean = MBeanServerInvocationHandler.newProxyInstance(connection, mbeanName, BrokerViewMBean.class, true);

			for (ObjectName on: mbean.getQueues()) {
				System./**/out.println("queue: " + on.getCanonicalName());
			}

			System./**/out.println("Id:" + mbean.getBrokerId());
		}
		catch (Exception ex) {
			throw new CommandException("....", ex);
		}
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
