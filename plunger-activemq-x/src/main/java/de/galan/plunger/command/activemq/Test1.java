package de.galan.plunger.command.activemq;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;


/** x */
public class Test1 {

	public static void main(String[] args) throws Exception {
		try {
			JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi"));
			connector.connect();
			MBeanServerConnection connection = connector.getMBeanServerConnection();

			ObjectName mbeanName = new ObjectName("org.apache.activemq:brokerName=localhost,type=Broker");
			BrokerViewMBean mbean = MBeanServerInvocationHandler.newProxyInstance(connection, mbeanName, BrokerViewMBean.class, true);

			for (ObjectName on: mbean.getQueues()) {
				System./**/out.println("queue: " + on.getCanonicalName());
			}

			System./**/out.println("Id:" + mbean.getBrokerId());
		}
		catch (Exception x) {
			x/**/./**/printStackTrace/**/();
		}

	}

}
