package de.galan.plunger.command.activemq;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.jms.AbstractJmsPutCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class AmqPutCommand extends AbstractJmsPutCommand {

	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		setJms(new AmqJms());
		super.initialize(pa);
	}

}
