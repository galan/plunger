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
		return "hornetq-2.2.x";
	}

	@Override
	public Command ls(PlungerArguments jca) {
		return new HornetqCommandLs();
	}


	@Override
	public Command cat(PlungerArguments jca) {
		return new HornetqCommandCat();
	}


	@Override
	public Command put(PlungerArguments jca) {
		return new HornetqCommandPut();
		//cmd = new ActivemqCommandLs();
	}


	@Override
	public Command count(PlungerArguments jca) {
		return new HornetqCommandCount();
	}

}
