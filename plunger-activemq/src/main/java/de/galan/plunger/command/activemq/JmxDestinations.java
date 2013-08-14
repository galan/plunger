package de.galan.plunger.command.activemq;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.management.ObjectName;

import org.apache.activemq.broker.jmx.BrokerViewMBean;


/**
 * Collects ActiveMQ destinations and is sorted and iterable.
 * 
 * @author daniel
 */
class JmxDestinations implements Iterable<JmxDestination> {

	Set<JmxDestination> destinations = new TreeSet<>();


	public JmxDestinations() {
		// nada
	}


	public JmxDestinations(BrokerViewMBean mbList) {
		addDestinations(mbList.getTemporaryQueues(), "Queue", true);
		addDestinations(mbList.getTemporaryTopics(), "Topic", true);
		addDestinations(mbList.getQueues(), "Queue", false);
		addDestinations(mbList.getTopics(), "Topic", false);
	}


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
