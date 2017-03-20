package de.galan.plunger.command;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.galan.plunger.domain.Message;
import de.galan.plunger.util.Escape;


/**
 * Marshalls and unmarshalles messages using the basic notation:<br/>
 * json-properties(tab)marshalled-body<br/>
 * eg.:<br/>
 * {"property":"value"}\tHello world
 *
 * @author daniel
 */
public class CompleteMessageMarshaller implements MessageMarshaller {

	private static final String SEPARATOR = "\t";


	public String getSeparator() {
		return SEPARATOR;
	}


	@Override
	public String marshal(Message message) {
		String[] parts = marshalParts(message);
		return parts[0] + parts[1] + parts[2];
	}


	@SuppressWarnings("unchecked")
	public String[] marshalParts(Message message) {
		String[] result = new String[3];
		JSONObject jo = new JSONObject();
		for (String key: message.getProperties().keySet()) {
			jo.put(key, message.getProperty(key));
		}
		result[0] = jo.toJSONString();
		result[1] = getSeparator();
		result[2] = new Escape().escape(message.getBody());
		return result;
	}


	@Override
	public Message unmarshal(String line) throws Exception {
		String json = StringUtils.substringBefore(line, getSeparator());
		String body = StringUtils.substringAfter(line, getSeparator());
		Message result = new Message();
		result.setBody(new Escape().unescape(body));
		JSONObject jo = (JSONObject)new JSONParser().parse(json);
		for (Object key: jo.keySet()) {
			result.putProperty((String)key, jo.get(key));
		}
		return result;
	}

}
