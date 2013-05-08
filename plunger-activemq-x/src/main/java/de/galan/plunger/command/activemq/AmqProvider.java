package de.galan.plunger.command.activemq;

import de.galan.plunger.command.Command;
import de.galan.plunger.command.CommandName;
import de.galan.plunger.command.CommandProvider;
import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class AmqProvider implements CommandProvider {

	@Override
	public String getName() {
		return "amq";
	}


	@Override
	public Command getCommand(CommandName commandName, PlungerArguments pa) {
		switch (commandName) {
			case LS:
				//return new AmqLsCommand();
			case CAT:
				return new AmqCatCommand();
			case PUT:
				return new AmqPutCommand();
			case COUNT:
				return new AmqCountCommand();
			default:
				return null;
		}
	}


	@Override
	public int getDefaultPort() {
		return 61616;
	}

}
