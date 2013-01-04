package de.galan.plunger.command;

/**
 * Exception while initializing
 * 
 * @author daniel
 */
public class InitializationException extends Exception {

	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}


	public InitializationException(String message) {
		super(message);
	}

}
