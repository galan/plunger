package de.galan.plunger.command.hornetq;


import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession.QueueQuery;

import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Counting the messages in a destination
 * 
 * @author daniel
 */
public class HornetqCommandCount extends AbstractHornetqCoreCommand {

	@Override
	public void process(PlungerArguments jca) {
		try {
			QueueQuery queueQuery = getSession().queueQuery(SimpleString.toSimpleString(jca.getDestination()));
			Output.println("" + queueQuery.getMessageCount());
		}
		catch (Exception ex) {
			Output.error("Failed command count: " + ex.getMessage());
		}
	}

}
