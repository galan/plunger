package de.galan.plunger.command.rabbitmq;

import static org.apache.commons.lang3.StringUtils.*;
import de.galan.plunger.command.jms.ProviderInformation;


/**
 * Common HornetQ abstractions.
 *
 * @author daniel
 */
public class RabbitmqUtil implements ProviderInformation {

	/*
	public TransportConfiguration getTransportConfiguration(PlungerArguments pa) {
		Map<String, Object> connectionMap = new HashMap<>();
		connectionMap.put(TransportConstants.HOST_PROP_NAME, pa.getTarget().getHost());
		connectionMap.put(TransportConstants.PORT_PROP_NAME, pa.getTarget().getPort());
		return new TransportConfiguration(NettyConnectorFactory.class.getName(), connectionMap);
	}
	 */

	@Override
	public boolean isSystemHeader(String headerName) {
		return startsWith(headerName, "JMS") || startsWith(headerName, "HQ");
	}

}
