package de.galan.plunger.command.stomp;

import de.galan.plunger.command.Command;
import de.galan.plunger.command.CommandName;
import de.galan.plunger.command.CommandProvider;
import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class StompProvider implements CommandProvider {

	@Override
	public String getName() {
		return "stomp";
	}


	@Override
	public Command getCommand(CommandName commandName, PlungerArguments pa) {
		switch (commandName) {
			case LS:
				return new StompLsCommand();
			case CAT:
				return new StompCatCommand();
			case PUT:
				return new StompPutCommand();
			case COUNT:
				return new StompCountCommand();
			default:
				return null;
		}
	}


	@Override
	public int getDefaultPort() {
		return 61613;
	}

}
