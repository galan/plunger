package de.galan.plunger.command.amqp;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.jms.AbstractJmsPutCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Writes messages to a destination on a HornetQ messaging server.
 *
 * @author daniel
 */
public class RabbitmqPutCommand extends AbstractJmsPutCommand {

	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		RabbitmqUtil util = new RabbitmqUtil();
		//setJms(new RabbitmqJms(util.getTransportConfiguration(pa)));
		super.initialize(pa);
	}

}
