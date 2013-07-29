package de.galan.plunger.command.activemq;

import static org.apache.commons.lang.StringUtils.*;

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
import de.galan.plunger.domain.Target;
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
	protected void process(PlungerArguments pa) throws CommandException {
		try {
			connector.connect();
			MBeanServerConnection connection = connector.getMBeanServerConnection();

			ObjectName mbeanName = new ObjectName("org.apache.activemq:brokerName=localhost,type=Broker");
			BrokerViewMBean mbean = MBeanServerInvocationHandler.newProxyInstance(connection, mbeanName, BrokerViewMBean.class, true);

			for (ObjectName on: mbean.getQueues()) {
				printDestination(pa, on.getKeyProperty("destinationName"), 0, 0, true);
			}
			//System./**/out.println("Id:" + mbean.getBrokerId());
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
