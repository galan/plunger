package de.galan.plunger.command.hornetq;

import de.galan.plunger.command.Command;
import de.galan.plunger.command.CommandProvider;
import de.galan.plunger.command.CommandName;
import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class HornetqProvider implements CommandProvider {

	@Override
	public String getName() {
		return "hornetq-2.2";
	}


	@Override
	public Command getCommand(CommandName commandName, PlungerArguments pa) {
		switch (commandName) {
			case LS:
				return new HornetqLsCommand();
			case CAT:
				return new HornetqCatCommand();
			case PUT:
				return new HornetqPutCommand();
			case COUNT:
				return new HornetqCountCommand();
			default:
				return null;
		}
	}


	@Override
	public int getDefaultPort() {
		return 5445;
	}

}
