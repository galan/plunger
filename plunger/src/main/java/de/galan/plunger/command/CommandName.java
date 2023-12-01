package de.galan.plunger.command;

import static org.apache.commons.lang3.StringUtils.*;


/**
 * Available commands.
 */
public enum CommandName {

	LS,
	CAT,
	PUT,
	COUNT;

	public static CommandName get(String command) {
		return isNotBlank(command) ? CommandName.valueOf(command.trim().toUpperCase()) : null;
	}

}
