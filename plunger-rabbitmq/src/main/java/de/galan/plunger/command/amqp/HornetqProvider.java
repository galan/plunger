package de.galan.plunger.command.amqp;

import de.galan.plunger.command.Command;
import de.galan.plunger.command.CommandName;
import de.galan.plunger.command.CommandProvider;
import de.galan.plunger.domain.PlungerArguments;


/**
 * CommandProvider for a RabbitMQ message-broker
 *
 * @author daniel
 */
public class HornetqProvider implements CommandProvider {

	@Override
	public String getName() {
		return "rabbitmq";
	}


	@Override
	public Command getCommand(CommandName commandName, PlungerArguments pa) {
		switch (commandName) {
			case LS:
				return new RabbitmqLsCommand();
			case CAT:
				return new RabbitmqCatCommand();
			case PUT:
				return new RabbitmqPutCommand();
			case COUNT:
				return new RabbitmqCountCommand();
			default:
				return null;
		}
	}


	@Override
	public int getDefaultPort() {
		return 5672;
	}

}
