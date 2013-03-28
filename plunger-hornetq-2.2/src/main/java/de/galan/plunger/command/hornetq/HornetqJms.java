package de.galan.plunger.command.hornetq;

import javax.jms.ConnectionFactory;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;

import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.protocol.jms.AbstractJms;


/**
 * HornetQs JMS-provider connectivity.
 * 
 * @author daniel
 */
public class HornetqJms extends AbstractJms {

	private TransportConfiguration transportConfiguration;


	public HornetqJms(TransportConfiguration transportConfiguration) {
		this.transportConfiguration = transportConfiguration;
	}


	@Override
	protected ConnectionFactory createConnectionFactory(PlungerArguments pa) {
		return (ConnectionFactory)HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
	}

}
