package de.galan.plunger.command.rabbitmq;

import static org.apache.commons.lang3.StringUtils.*;
import de.galan.commons.net.UrlUtil;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Common HornetQ abstractions.
 *
 * @author daniel
 */
public class RabbitmqUtil {

	/*
	public TransportConfiguration getTransportConfiguration(PlungerArguments pa) {
		Map<String, Object> connectionMap = new HashMap<>();
		connectionMap.put(TransportConstants.HOST_PROP_NAME, pa.getTarget().getHost());
		connectionMap.put(TransportConstants.PORT_PROP_NAME, pa.getTarget().getPort());
		return new TransportConfiguration(NettyConnectorFactory.class.getName(), connectionMap);
	}
	 */

	public static boolean isSystemHeader(String headerName) {
		return startsWith(headerName, "JMS") || startsWith(headerName, "HQ");
	}


	public static String getBase64Vhost(PlungerArguments pa) {
		return UrlUtil.encode(defaultString(pa.getTarget().getParameterValue("vhost"), "/"));
	}

}
