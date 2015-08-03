package de.galan.plunger.command.rabbitmq;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi.Color;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.galan.flux.FluentHttpClient.HttpBuilder;
import de.galan.flux.Flux;
import de.galan.flux.HttpClientException;
import de.galan.flux.Response;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractLsCommand;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.domain.Target;
import de.galan.plunger.util.Output;


/**
 * Lists all destinations on a HornetQ messaging server.
 *
 * @author daniel
 */
public class RabbitmqLsCommand extends AbstractLsCommand {

	RabbitmqCore core;
	RabbitmqUtil util; // TODO useable?
	ObjectMapper mapper;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		util = new RabbitmqUtil();
		core = new RabbitmqCore();
		mapper = new ObjectMapper();
	}


	@Override
	protected void process(PlungerArguments pa) throws CommandException {
		List<Item> items = new ArrayList<>();
		try {
			items.addAll(collectQueues(pa));
			items.addAll(collectExchanges(pa));
		}
		catch (HttpClientException ex) {
			throw new CommandException("Unable to retrieve queues from RabbitMQs API (management extension must be installed)", ex);
		}
		catch (IOException ex) {
			throw new CommandException("Unable to process response", ex);
		}

		items.sort(Comparator.comparing(i -> i.name));
		for (Item item: items) {
			if (StringUtils.equals("queue", item.entity)) {
				printDestination(pa, item.name, item.consumer, item.messages, item.durable);
			}
			else {
				printExchange(pa, item.name, item.type, item.durable);
			}
		}

	}


	private void printExchange(PlungerArguments pa, String name, String type, boolean persistent) {
		boolean filterPersistent = pa.containsCommandArgument("p");
		boolean optionMessages = pa.containsCommandArgument("m");
		boolean optionConsumer = pa.containsCommandArgument("c");

		boolean filterPassed = (persistent && !filterPersistent);
		if (filterPassed && !optionMessages && !optionConsumer) {
			Color exchangeColor = Color.GREEN;
			Output.print(exchangeColor, name);
			if (!pa.containsCommandArgument("i")) {
				if (!persistent) {
					Output.print(" (temporary)");
				}
				Output.print(" (");
				Output.print(Color.BLUE, type);
				Output.print(")");
			}
			Output.println("");
		}
	}


	@Override
	protected Color getDestinationColor(String name) {
		return Color.CYAN;
	}


	private void x(String url) {
		//String url = "http://" + t.getHost() + ":" + mgmtPort + "/api/queues";
		//Unirest.get("http://" + t.getHost() + ":" + mgmtPort + "/api/queues").asJson().getBody();
	}


	private List<Item> collectQueues(PlungerArguments pa) throws HttpClientException, IOException {
		List<Item> result = new ArrayList<>();

		Target t = pa.getTarget();
		String mgmtPort = t.getParameterValue("managementPort");
		/*
		try {
			HttpResponse<String> response2 = Unirest.get("http://" + t.getHost() + ":" + mgmtPort + "/api/queues").basicAuth(t.getUsername(), t.getPassword()).asString();
		}
		catch (UnirestException ex) {
			//Say.warn("Unspecified error from daniel", ex);
		}
		 */

		HttpBuilder request = Flux.request("http://" + t.getHost() + ":" + mgmtPort + "/api/queues");
		if (isNotBlank(t.getUsername()) && isNotBlank(t.getPassword())) {
			request.authentication(t.getUsername(), t.getPassword());
		}
		try (Response response = request.get()) {
			ArrayNode queueNodes = (ArrayNode)mapper.readTree(response.getStream());
			for (JsonNode node: queueNodes) {
				Item item = new Item();
				item.entity = "queue";
				item.vhost = node.get("vhost").textValue();
				item.name = node.get("name").textValue();
				item.messages = node.get("messages_ready").longValue();
				item.consumer = node.get("consumers").longValue();
				item.durable = node.get("durable").booleanValue();
				result.add(item);
			}
		}
		return result;
	}


	private List<Item> collectExchanges(PlungerArguments pa) throws HttpClientException, IOException {
		List<Item> result = new ArrayList<>();

		Target t = pa.getTarget();
		String mgmtPort = t.getParameterValue("managementPort");
		HttpBuilder request = Flux.request("http://" + t.getHost() + ":" + mgmtPort + "/api/exchanges");
		if (isNotBlank(t.getUsername()) && isNotBlank(t.getPassword())) {
			request.authentication(t.getUsername(), t.getPassword());
		}
		try (Response response = request.get()) {
			ArrayNode queueNodes = (ArrayNode)mapper.readTree(response.getStream());
			for (JsonNode node: queueNodes) {
				Item item = new Item();
				item.entity = "exchange";
				item.vhost = node.get("vhost").textValue();
				item.name = node.get("name").textValue();
				item.type = node.get("type").textValue();
				item.durable = node.get("durable").booleanValue();
				boolean internal = node.get("internal").booleanValue();
				if (!internal && isNotBlank(item.name)) {
					result.add(item);
				}
			}
		}
		return result;
	}


	@Override
	protected void close() {
		core.close();
	}

}


/** Item to be shown in the list */
class Item {

	String entity;
	String vhost;
	String name;
	String type;
	Long consumer;
	Long messages;
	Boolean durable;
}
