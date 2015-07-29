package de.galan.plunger.command.rabbitmq;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCountCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Counts messags from a HornetQ messaging server.
 *
 * @author daniel
 */
public class RabbitmqCountCommand extends AbstractCountCommand {

	RabbitmqCore core;
	RabbitmqUtil util;


	@Override
	protected long getCount(PlungerArguments pa) throws CommandException {
		/*
		try {
			QueueQuery queueQuery = core.getSession().queueQuery(SimpleString.toSimpleString("jms." + pa.getTarget().getDestination()));
			return pa.containsCommandArgument("c") ? queueQuery.getConsumerCount() : queueQuery.getMessageCount();
		}
		catch (Exception ex) {
			throw new CommandException("Failed retrieving count", ex);
		}
		 */
		return 0L;
	}


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		util = new RabbitmqUtil();
		core = new RabbitmqCore();
		//core.initialize(pa, util.getTransportConfiguration(pa));
		//super.initialize(pa);
	}


	@Override
	protected void close() {
		core.close();
	}

}
