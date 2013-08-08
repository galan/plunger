package de.galan.plunger.command.activemq;

import static org.apache.commons.lang.StringUtils.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.commons.lang.ObjectUtils;

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

			ObjectName nameList = new ObjectName("org.apache.activemq:brokerName=localhost,type=Broker");
			BrokerViewMBean mbList = MBeanServerInvocationHandler.newProxyInstance(connection, nameList, BrokerViewMBean.class, true);

			for (JmxDestination jd: collectDestinations(mbList)) {
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
	}


	protected JmxDestinations collectDestinations(BrokerViewMBean mbList) {
		JmxDestinations jmx = new JmxDestinations();
		jmx.addDestinations(mbList.getTemporaryQueues(), "Queue", true);
		jmx.addDestinations(mbList.getTemporaryTopics(), "Topic", true);
		jmx.addDestinations(mbList.getQueues(), "Queue", false);
		jmx.addDestinations(mbList.getTopics(), "Topic", false);
		return jmx;
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


/** x */
class JmxDestinations implements Iterable<JmxDestination> {

	Set<JmxDestination> destinations = new TreeSet<>();


	public void addDestinations(ObjectName[] names, String destinationType, boolean temporary) {
		if (names != null && names.length > 0) {
			for (ObjectName destination: names) {
				String destinationName = destination.getKeyProperty("destinationName");
				destinations.add(new JmxDestination(destinationName, destinationType, temporary));
			}
		}
	}


	@Override
	public Iterator<JmxDestination> iterator() {
		return destinations.iterator();
	}

}


/** y */
class JmxDestination implements Comparable<JmxDestination> {

	private String objectName;
	private String destinationType;
	private boolean temporary;
	private String displayName;


	public JmxDestination(String objectName, String destinationType, boolean temporary) {
		this.objectName = objectName;
		this.destinationType = destinationType;
		this.temporary = temporary;
		displayName = lowerCase(destinationType) + "." + objectName;
	}


	public String getObjectName() {
		return objectName;
	}


	public String getDestinationType() {
		return destinationType;
	}


	public boolean isTemporary() {
		return temporary;
	}


	public String getDisplayName() {
		return displayName;
	}


	@Override
	public int compareTo(JmxDestination o) {
		return ObjectUtils.compare(getDisplayName(), o.getDisplayName());
	}

}
