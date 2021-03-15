package de.galan.plunger.command;

import de.galan.plunger.domain.PlungerArguments;


/**
 * A command plunger could execute
 * 
 * @author daniel
 */
public interface Command {

	public void execute(PlungerArguments pa) throws CommandException;

	default void onError(CommandException exception) {}
}
