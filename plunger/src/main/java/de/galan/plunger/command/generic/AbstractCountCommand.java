package de.galan.plunger.command.generic;

import de.galan.plunger.command.AbstractCommand;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Generic count command, that handles most of the plunger arguments already.
 * 
 * @author daniel
 */
public abstract class AbstractCountCommand extends AbstractCommand {

	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		if (!pa.getTarget().hasDestination()) {
			throw new CommandException("No destination has been specified");
		}
	}


	@Override
	protected void process(PlungerArguments pa) throws CommandException {
		Output.println("" + getCount(pa));
	}


	protected abstract long getCount(PlungerArguments pa) throws CommandException;

}
