package de.galan.plunger.command.rabbitmq;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi.Color;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
	ObjectMapper mapper;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		core = new RabbitmqCore();
		mapper = new ObjectMapper();
	}


	@Override
	protected void process(PlungerArguments pa) throws CommandException {
		String vhost = pa.getCommandArgument("vhost");

		List<Item> items = new ArrayList<>();
		try {
			items.addAll(collectQueues(pa));
			items.addAll(collectExchanges(pa));
		}
		catch (IOException ex) {
			throw new CommandException("Unable to process response", ex);
		}

		items.sort(Comparator.comparing(i -> i.name));
		for (Item item: items) {
			boolean matchesTarget = pa.getTarget().isDestinationErased() || item.name.equals(pa.getTarget().getDestination());
			boolean matchesVhost = isBlank(vhost) || StringUtils.equals(vhost, item.vhost);
			if (matchesTarget && matchesVhost) {
				if (StringUtils.equals("queue", item.entity)) {
					printDestination(pa, item.name, item.consumer, item.messages, item.durable);
				}
				else {
					printExchange(pa, item.name, item.type, item.durable);
				}
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


	protected String readUrl(String url, String username, String password) throws IOException {
		String result = null;
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)new URL(url).openConnection();
			if (isNotBlank(username) && isNotBlank(password)) {
				String pair = username + ":" + password;
				String encodedAuthorization = trim(Base64.encodeBase64String(pair.getBytes(Charsets.UTF_8)));
				connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
			}
			//connection.setConnectTimeout(..);
			//connection.setReadTimeout(..);
			connection.setRequestMethod("GET");
			//int statusCode = connection.getResponseCode();
			InputStream stream = connection.getInputStream();
			result = IOUtils.toString(stream, Charsets.UTF_8);
			stream.close();
		}
		catch (Exception ex) {
			throw new IOException(ex);
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return result;
	}


	private List<Item> collectQueues(PlungerArguments pa) throws IOException {
		List<Item> result = new ArrayList<>();

		Target t = pa.getTarget();
		String mgmtPort = t.getParameterValue("managementPort");
		String response = readUrl("http://" + t.getHost() + ":" + mgmtPort + "/api/queues", t.getUsername(), t.getPassword());
		ArrayNode queueNodes = (ArrayNode)mapper.readTree(response);
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
		return result;
	}


	private List<Item> collectExchanges(PlungerArguments pa) throws IOException {
		List<Item> result = new ArrayList<>();
		Target t = pa.getTarget();
		String mgmtPort = t.getParameterValue("managementPort");
		String response = readUrl("http://" + t.getHost() + ":" + mgmtPort + "/api/exchanges", t.getUsername(), t.getPassword());
		ArrayNode queueNodes = (ArrayNode)mapper.readTree(response);
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
