package de.galan.plunger.command;

import static org.apache.commons.lang.StringUtils.*;


/**
 * In plunger available commands. These should be and behave the same (as far as possible) on all providers.
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
