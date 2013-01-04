package de.galan.plunger.command;

import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Generic Command that will be executed when the specified Command is not registered.
 * 
 * @author daniel
 */
public class UnsupportedCommand implements Command {

	@Override
	public void execute(PlungerArguments pa) {
		Output.error("unsupported command");
	}

}
