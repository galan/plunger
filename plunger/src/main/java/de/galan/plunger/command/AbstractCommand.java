package de.galan.plunger.command;

import de.galan.plunger.domain.PlungerArguments;


/**
 * Abstraction for Command interface
 * 
 * @author daniel
 */
public abstract class AbstractCommand implements Command {

	@Override
	public void execute(PlungerArguments pa) throws CommandException {
		try {
			initialize(pa);
			process(pa);
		}
		finally {
			close();
		}
	}


	protected abstract void initialize(PlungerArguments pa) throws CommandException;


	protected abstract void process(PlungerArguments pa) throws CommandException;


	protected abstract void close();

}
