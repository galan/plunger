package de.galan.plunger.command.rabbitmq;

import static org.apache.commons.lang3.StringUtils.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.galan.commons.net.UrlUtil;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCountCommand;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.domain.Target;
import de.galan.plunger.util.Urls;


/**
 * Counts messags from a HornetQ messaging server.
 *
 * @author daniel
 */
public class RabbitmqCountCommand extends AbstractCountCommand {

	RabbitmqCore core;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		core = new RabbitmqCore();
	}


	@Override
	protected long getCount(PlungerArguments pa) throws CommandException {
		Target t = pa.getTarget();
		if (isBlank(t.getDestination())) {
			throw new CommandException("No queue given");
		}
		String mgmtPort = t.getParameterValue("managementPort");
		if (!isNumeric(mgmtPort)) {
			throw new CommandException("No managementPort provided");
		}
		try {
			String vhost = RabbitmqUtil.getBase64Vhost(pa);
			String destination = "/" + UrlUtil.encode(t.getDestination());
			String urlString = "http://" + t.getHost() + ":" + mgmtPort + "/api/queues/" + vhost + destination;
			String response = Urls.read(urlString, t.getUsername(), t.getPassword());
			ObjectNode tree = (ObjectNode)new ObjectMapper().readTree(response);
			return tree.get(pa.containsCommandArgument("c") ? "consumers" : "messages").asLong(0L);
		}
		catch (Exception ex) {
			throw new CommandException("Failed retrieving count", ex);
		}

	}


	@Override
	protected void close() {
		core.close();
	}

}
