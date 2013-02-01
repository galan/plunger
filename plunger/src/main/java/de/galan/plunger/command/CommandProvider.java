package de.galan.plunger.command;

import de.galan.plunger.domain.PlungerArguments;


/**
 * Messaging Provider.
 * 
 * @author daniel
 */
public interface CommandProvider {

	public String getName();


	public Command ls(PlungerArguments pa);


	public Command cat(PlungerArguments pa);


	public Command put(PlungerArguments pa);


	public Command count(PlungerArguments pa);

	//public Command execute(CommandName<<enum name, PlungerArguments pa);
	//public int getDefaultPort(); ?

}
