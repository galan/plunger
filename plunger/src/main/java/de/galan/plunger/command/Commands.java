package de.galan.plunger.command;

import static org.apache.commons.lang.StringUtils.*;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public enum Commands {

	LS,
	CAT,
	PUT,
	COUNT;

	public static Commands get(String command) {
		return isNotBlank(command) ? Commands.valueOf(command.trim().toUpperCase()) : null;
	}

}
