package de.galan.plunger.command.hornetq;

import de.galan.plunger.command.Command;
import de.galan.plunger.command.CommandProvider;
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
	public Command ls(PlungerArguments pa) {
		return new HornetqLsCommand();
	}


	@Override
	public Command cat(PlungerArguments pa) {
		//return new HornetqCommandCat();
		return new HornetqCatCommand();
	}


	@Override
	public Command put(PlungerArguments pa) {
		return new HornetqPutCommand();
	}


	@Override
	public Command count(PlungerArguments pa) {
		return new HornetqCountCommand();
	}

}
