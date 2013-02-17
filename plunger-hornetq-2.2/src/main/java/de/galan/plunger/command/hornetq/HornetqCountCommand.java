package de.galan.plunger.command.hornetq;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession.QueueQuery;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCountCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class HornetqCountCommand extends AbstractCountCommand {

	HornetqCore core;
	HornetqUtil util;


	@Override
	protected long getCount(PlungerArguments pa) throws CommandException {
		try {
			QueueQuery queueQuery = core.getSession().queueQuery(SimpleString.toSimpleString(pa.getDestination()));
			return queueQuery.getMessageCount();
		}
		catch (Exception ex) {
			throw new CommandException("Failed retrieving count", ex);
		}
	}


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		util = new HornetqUtil();
		core = new HornetqCore();
		core.initialize(pa, util.getTransportConfiguration(pa));
	}


	@Override
	protected void close() {
		core.close();
	}

}
