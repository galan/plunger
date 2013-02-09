package de.galan.plunger.command.generic;

import de.galan.plunger.command.AbstractCommand;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public abstract class AbstractCountCommand extends AbstractCommand {

	@Override
	protected void process(PlungerArguments pa) throws CommandException {
		Output.println("" + getCount(pa));
	}


	protected abstract long getCount(PlungerArguments pa) throws CommandException;

}
