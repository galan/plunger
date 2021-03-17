package de.galan.plunger.command;

import de.galan.plunger.domain.PlungerArguments;


/**
 * A command plunger could execute
 */
public interface Command {

	public void execute(PlungerArguments pa) throws CommandException;


	default void onError(CommandException exception) {
		// To be implemented by commands that need an error-handling after a CommandException is thrown
	}

}
