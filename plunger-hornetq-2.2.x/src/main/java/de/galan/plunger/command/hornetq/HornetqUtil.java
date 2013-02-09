package de.galan.plunger.command.hornetq;

import static org.apache.commons.lang.StringUtils.*;

import java.util.HashMap;
import java.util.Map;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;

import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class HornetqUtil {

	public TransportConfiguration getTransportConfiguration(PlungerArguments pa) {
		Map<String, Object> connectionMap = new HashMap<>();
		connectionMap.put(TransportConstants.HOST_PROP_NAME, pa.getTarget().getHost());
		connectionMap.put(TransportConstants.PORT_PROP_NAME, pa.getTarget().getPort());
		return new TransportConfiguration(NettyConnectorFactory.class.getName(), connectionMap);
	}


	public boolean isSystemHeader(String headerName) {
		return startsWith(headerName, "JMS") || startsWith(headerName, "HQ");
	}

}
