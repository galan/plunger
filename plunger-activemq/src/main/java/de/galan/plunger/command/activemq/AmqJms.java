package de.galan.plunger.command.activemq;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

import de.galan.plunger.command.jms.AbstractJms;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.domain.Target;


/**
 * ActiveMQs JMS-provider connectivity.
 */
public class AmqJms extends AbstractJms {

	@Override
	protected ConnectionFactory createConnectionFactory(PlungerArguments pa) {
		Target t = pa.getTarget();
		String url = "tcp://" + t.getHost() + ":" + t.getPort();
		return new ActiveMQConnectionFactory(url);
	}

}
