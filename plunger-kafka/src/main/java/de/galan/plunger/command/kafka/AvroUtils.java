package de.galan.plunger.command.kafka;

import static org.apache.commons.lang3.StringUtils.*;

import org.apache.avro.Schema;
import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.galan.commons.net.flux.FluentHttpClient.HttpBuilder;
import de.galan.commons.net.flux.Flux;
import de.galan.commons.net.flux.Response;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Functionality used by Kafka Put and Cat in case a schema registry with Avro is used
 */
public class AvroUtils {

	public static Schema getSchema(String schemaRegistry, String topic) throws CommandException {
		HttpBuilder http = Flux.request(schemaRegistry + "/subjects/" + topic + "-value/versions/latest");
		try (Response response = http.get()) {
			String content = response.getStreamAsString();

			Schema.Parser schemaParser = new Schema.Parser();
			JsonNode json = new ObjectMapper().readTree(content);
			String schemaString = StringEscapeUtils.unescapeJson(json.get("schema").asText());
			return schemaParser.parse(schemaString);
		}
		catch (Exception ex) {
			throw new CommandException("Could not fetch Avro Schema", ex);
		}
	}


	public static String determineSchemaRegistry(PlungerArguments pa) {
		String param = pa.getTarget().getParameterValue("schemaRegistry");
		if (isNotBlank(param)) {
			if (!startsWithAny(param, "http://", "https://")) {
				param = "http://" + param;
			}
		}
		return param;
	}

}
