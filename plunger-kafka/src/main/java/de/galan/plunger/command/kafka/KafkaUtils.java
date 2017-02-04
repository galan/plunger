package de.galan.plunger.command.kafka;

import static de.galan.commons.util.Sugar.*;

import de.galan.plunger.domain.Target;


/**
 * Functionality required by all kafka commands
 */
public class KafkaUtils {

	public static String brokers(Target target) {
		String single = target.getHost() + ":" + target.getPort();
		String brokers = target.getParameterValue("brokers");
		return optional(brokers).orElse(single);
	}

}
