package de.galan.plunger.command.hornetq;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.jms.AbstractJmsPutCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Writes messages to a destination on a HornetQ messaging server.
 */
public class HornetqPutCommand extends AbstractJmsPutCommand {

	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		HornetqUtil util = new HornetqUtil();
		setJms(new HornetqJms(util.getTransportConfiguration(pa)));
		super.initialize(pa);
	}

}
