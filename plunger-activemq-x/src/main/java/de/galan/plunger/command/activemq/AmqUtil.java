package de.galan.plunger.command.activemq;

import static org.apache.commons.lang.StringUtils.*;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class AmqUtil {

	public boolean isSystemHeader(String headerName) {
		return startsWith(headerName, "JMS") || startsWith(headerName, "HQ");
	}

}
