package de.galan.plunger.command.hornetq.obsolete;

import java.util.HashMap;
import java.util.Map;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;

import de.galan.plunger.command.AbstractCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Hornetq Command abstraction.
 * 
 * @author daniel
 */
public abstract class AbstractHornetqCommand extends AbstractCommand {

	protected TransportConfiguration getTransportConfiguration(PlungerArguments pa) {
		Map<String, Object> connectionMap = new HashMap<>();
		connectionMap.put(TransportConstants.HOST_PROP_NAME, pa.getTarget().getHost());
		connectionMap.put(TransportConstants.PORT_PROP_NAME, pa.getTarget().getPort());
		return new TransportConfiguration(NettyConnectorFactory.class.getName(), connectionMap);
	}

}
