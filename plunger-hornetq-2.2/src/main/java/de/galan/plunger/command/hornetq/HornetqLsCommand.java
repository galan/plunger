package de.galan.plunger.command.hornetq;

import java.util.Arrays;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientRequestor;
import org.hornetq.api.core.client.ClientSession.QueueQuery;
import org.hornetq.api.core.management.ManagementHelper;
import org.hornetq.api.core.management.ResourceNames;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractLsCommand;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.StringCaseInsensitiveComparator;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class HornetqLsCommand extends AbstractLsCommand {

	HornetqCore core;
	HornetqUtil util;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		util = new HornetqUtil();
		core = new HornetqCore();
		core.initialize(pa, util.getTransportConfiguration(pa));
	}


	@Override
	protected void process(PlungerArguments pa) throws CommandException {
		try {
			ClientRequestor requestor = new ClientRequestor(core.getSession(), "jms.queue.hornetq.management");
			ClientMessage message = core.getSession().createMessage(false);
			ManagementHelper.putAttribute(message, ResourceNames.CORE_SERVER, "addressNames");
			ClientMessage reply = requestor.request(message);
			Object[] arrayObjects = (Object[])ManagementHelper.getResult(reply);
			//TODO if destination is given, list only single destination
			String[] array = Arrays.copyOf(arrayObjects, arrayObjects.length, String[].class);
			Arrays.sort(array, new StringCaseInsensitiveComparator());
			for (String address: array) {
				QueueQuery queueQuery = core.getSession().queueQuery(SimpleString.toSimpleString(address));
				printDestination(pa, address, queueQuery.getConsumerCount(), queueQuery.getMessageCount(), queueQuery.isDurable());
			}
		}
		catch (Exception ex) {
			throw new CommandException("Could not process ls", ex);
		}
	}


	@Override
	protected void close() {
		core.close();
	}

}
