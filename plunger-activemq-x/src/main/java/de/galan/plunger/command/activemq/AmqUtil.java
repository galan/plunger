package de.galan.plunger.command.activemq;

import static org.apache.commons.lang.StringUtils.*;
import de.galan.plunger.command.jms.ProviderInformation;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class AmqUtil implements ProviderInformation {

	@Override
	public boolean isSystemHeader(String headerName) {
		return startsWith(headerName, "JMS");
	}

}
