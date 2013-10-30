package de.galan.plunger.command.stomp;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractPutCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class StompPutCommand extends AbstractPutCommand {

	@Override
	protected void sendMessage(PlungerArguments pa, Message message, long count) throws CommandException {
	}


	@Override
	protected void close() {
	}

}
