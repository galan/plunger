package de.galan.plunger.command;

import de.galan.plunger.domain.PlungerArguments;


/**
 * Contract for a messaging provider.
 */
public interface CommandProvider {

	/** The name of the CommandProvider, used in target URI and therefore locating the SPI. */
	public String getName();


	/** Returns an executable command, defined by the CommandName */
	public Command getCommand(CommandName commandName, PlungerArguments pa);


	/** The port, the messaging provider typically uses (when omitted). */
	public int getDefaultPort();

}
