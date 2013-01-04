package de.galan.plunger.command;

import de.galan.plunger.domain.PlungerArguments;


/**
 * A Command plunger could execute
 * 
 * @author daniel
 */
public interface Command {

	public void execute(PlungerArguments pa) throws CommandException;

}
