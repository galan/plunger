package de.galan.plunger.command.hornetq;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.jms.AbstractJmsCatCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Prints messags from a HornetQ messaging server.
 * 
 * @author daniel
 */
public class HornetqCatCommand extends AbstractJmsCatCommand {

	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		HornetqUtil util = new HornetqUtil();
		setProviderInformation(util);
		setJms(new HornetqJms(util.getTransportConfiguration(pa)));
		super.initialize(pa);
	}

}
