package de.galan.plunger.command.kafka;

import de.galan.plunger.command.Command;
import de.galan.plunger.command.CommandName;
import de.galan.plunger.command.CommandProvider;
import de.galan.plunger.domain.PlungerArguments;


/**
 * CommandProvider for a RabbitMQ message-broker
 *
 * @author daniel
 */
public class KafkaProvider implements CommandProvider {

	@Override
	public String getName() {
		return "kafka";
	}


	@Override
	public Command getCommand(CommandName commandName, PlungerArguments pa) {
		switch (commandName) {
			case LS:
				return new KafkaLsCommand();
			case CAT:
				return new KafkaCatCommand();
			case PUT:
				return new KafkaPutCommand();
			case COUNT:
				return new KafkaCountCommand();
			default:
				return null;
		}
	}


	@Override
	public int getDefaultPort() {
		return 9092;
	}

}
