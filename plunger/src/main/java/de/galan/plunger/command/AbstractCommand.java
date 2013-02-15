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
		initialize(pa);
		// In case SIGINT/SIGTERM is received, as well as normal self termination
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				close();
			}
		}));
		process(pa);
	}


	protected abstract void initialize(PlungerArguments pa) throws CommandException;


	protected abstract void process(PlungerArguments pa) throws CommandException;


	protected abstract void close();

}
