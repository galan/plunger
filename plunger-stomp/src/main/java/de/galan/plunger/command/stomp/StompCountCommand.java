package de.galan.plunger.command.stomp;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCountCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class StompCountCommand extends AbstractCountCommand {

	@Override
	protected long getCount(PlungerArguments pa) throws CommandException {
		return 0;
	}


	@Override
	protected void close() {
	}

}
