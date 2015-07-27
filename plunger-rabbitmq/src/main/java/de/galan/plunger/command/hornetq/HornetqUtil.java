package de.galan.plunger.command.hornetq;

import static org.apache.commons.lang.StringUtils.*;

import java.util.HashMap;
import java.util.Map;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;

import de.galan.plunger.command.jms.ProviderInformation;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Common HornetQ abstractions.
 * 
 * @author daniel
 */
public class HornetqUtil implements ProviderInformation {

	public TransportConfiguration getTransportConfiguration(PlungerArguments pa) {
		Map<String, Object> connectionMap = new HashMap<>();
		connectionMap.put(TransportConstants.HOST_PROP_NAME, pa.getTarget().getHost());
		connectionMap.put(TransportConstants.PORT_PROP_NAME, pa.getTarget().getPort());
		return new TransportConfiguration(NettyConnectorFactory.class.getName(), connectionMap);
	}


	@Override
	public boolean isSystemHeader(String headerName) {
		return startsWith(headerName, "JMS") || startsWith(headerName, "HQ");
	}

}
