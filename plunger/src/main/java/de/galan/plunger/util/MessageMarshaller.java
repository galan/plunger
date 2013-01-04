package de.galan.plunger.util;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.galan.plunger.domain.Message;


/**
 * Marshalls message
 * 
 * @author daniel
 */
public class MessageMarshaller {

	private static final String SEPARATOR = "\t";


	public String getSeparator() {
		return SEPARATOR;
	}


	public String marshal(Message message) {
		String[] parts = marshalParts(message);
		return parts[0] + parts[1] + parts[2];
	}


	@SuppressWarnings("unchecked")
	public String[] marshalParts(Message message) {
		//JSONObject jo = new JSONObject(message.getProperties());
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


	public Message unmarshal(String line) {
		String json = StringUtils.substringBefore(line, getSeparator());
		String body = StringUtils.substringAfter(line, getSeparator());
		Message result = new Message();
		result.setBody(new Escape().unescape(body));
		try {
			JSONObject jo = (JSONObject)new JSONParser().parse(json);
			for (Object key: jo.keySet()) {
				result.putProperty((String)key, jo.get(key));
			}
		}
		catch (ParseException ex) {
			//TODO
			Output.error("xxx" + ExceptionUtils.getStackTrace(ex));
		}
		return result;
	}

}
