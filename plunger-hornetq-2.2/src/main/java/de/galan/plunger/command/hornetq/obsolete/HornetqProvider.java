package de.galan.plunger.command.hornetq.obsolete;

import de.galan.plunger.command.Command;
import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class HornetqProvider {//implements CommandProvider {

	public String getName() {
		return "hornetq-2.2.x";
	}


	public Command ls(PlungerArguments pa) {
		return new HornetqCommandLs();
	}


	public Command cat(PlungerArguments pa) {
		return new HornetqCommandCat();
	}


	public Command put(PlungerArguments pa) {
		return new HornetqCommandPut();
	}


	public Command count(PlungerArguments pa) {
		return new HornetqCommandCount();
	}

}
