package de.galan.plunger.command;

/**
 * Generic Command Exception
 * 
 * @author daniel
 */
public class CommandException extends Exception {

	public CommandException(String message, Throwable cause) {
		super(message, cause);
	}


	public CommandException(String message) {
		super(message);
	}

}
