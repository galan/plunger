package de.galan.plunger.command.activemq;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.jms.AbstractJmsCatCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Prints messags from a ActiveMQ messaging server.
 * 
 * @author daniel
 */
public class AmqCatCommand extends AbstractJmsCatCommand {

	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		AmqUtil util = new AmqUtil();
		setProviderInformation(util);
		setJms(new AmqJms());
		super.initialize(pa);
	}

}
