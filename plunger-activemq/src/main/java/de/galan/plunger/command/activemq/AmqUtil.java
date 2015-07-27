package de.galan.plunger.command.activemq;

import static org.apache.commons.lang3.StringUtils.*;
import de.galan.plunger.command.jms.ProviderInformation;


/**
 * Helper for ActiveMQ.
 *
 * @author daniel
 */
public class AmqUtil implements ProviderInformation {

	@Override
	public boolean isSystemHeader(String headerName) {
		return startsWith(headerName, "JMS");
	}

}
