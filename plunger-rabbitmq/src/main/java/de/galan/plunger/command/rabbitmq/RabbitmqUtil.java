package de.galan.plunger.command.rabbitmq;

import static org.apache.commons.lang3.StringUtils.*;

import de.galan.commons.net.UrlUtil;
import de.galan.commons.util.Contained;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Common RabbitMQ abstractions.
 */
public class RabbitmqUtil {

	public static boolean isSystemHeader(String headerName) {
		return Contained.inObj(headerName, EMPTY, "amq.direct", "amq.fanout", "amq.headers", "amq.match", "amq.topic");
	}


	public static String getBase64Vhost(PlungerArguments pa) {
		return UrlUtil.encode(defaultString(pa.getTarget().getParameterValue("vhost"), "/"));
	}

}
