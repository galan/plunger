package de.galan.plunger.command.kafka;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.avro.Schema;
import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.PlungerArguments;


/**
 * Functionality used by Kafka Put and Cat in case a schema registry with Avro is used
 */
public class AvroUtils {

	public static Schema getSchema(String schemaRegistry, String topic) throws CommandException {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(schemaRegistry + "/subjects/" + topic + "-value/versions/latest");
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder content = new StringBuilder();
			while((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			Schema.Parser schemaParser = new Schema.Parser();
			JsonNode jsonNode = new ObjectMapper().readTree(content.toString());
			String schemaString = StringEscapeUtils.unescapeJson(jsonNode.get("schema")
																		 .asText());
			return schemaParser.parse(schemaString);
		}
		catch (Exception e) {
			throw new CommandException("Could not fetch Avro Schema", e);
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}


	public static String getSchemaRegistry(PlungerArguments pa) {
		String param = pa.getTarget().getParameterValue("schemaRegistry");
		if (isNotBlank(param)) {
			return param;
		}
		return null;
	}
}
