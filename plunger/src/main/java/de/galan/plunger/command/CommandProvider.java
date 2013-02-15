package de.galan.plunger.command;

import de.galan.plunger.domain.PlungerArguments;


/**
 * Messaging Provider.
 * 
 * @author daniel
 */
public interface CommandProvider {

	public String getName();


	public Command getCommand(CommandName commandName, PlungerArguments pa);


	public int getDefaultPort();

}
