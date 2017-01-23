package de.galan.plunger.command.kafka;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCountCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Counts messags from a Kafka messaging server -> Not implemented
 */
public class KafkaCountCommand extends AbstractCountCommand {

	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		// nada
	}


	@Override
	protected long getCount(PlungerArguments pa) throws CommandException {
		throw new CommandException("Not implementable for kafka");
	}


	@Override
	protected void close() {
		// nada
	}

}
