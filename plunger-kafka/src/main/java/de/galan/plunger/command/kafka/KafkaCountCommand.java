package de.galan.plunger.command.kafka;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCountCommand;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Counts messags from a HornetQ messaging server.
 *
 * @author daniel
 */
public class KafkaCountCommand extends AbstractCountCommand {

	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		//core = new RabbitmqCore();
	}


	@Override
	protected long getCount(PlungerArguments pa) throws CommandException {
		/*
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
		*/
		return 0;
	}


	@Override
	protected void close() {
		//core.close();
	}

}
