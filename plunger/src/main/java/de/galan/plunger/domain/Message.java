package de.galan.plunger.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Simplified Pojo for a message
 * 
 * @author daniel
 */
public class Message {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";

	private String body;
	private Map<String, Object> properties;


	public Map<String, Object> getProperties() {
		if (properties == null) {
			properties = new HashMap<>();
		}
		return properties;
	}


	public int getPropertiesSize() {
		return getProperties().size();
	}


	public Object getProperty(String key) {
		return getProperties().get(key);
	}


	public String getPropertyString(String key) {
		Object value = getProperty(key);
		if (value != null) {
			if (value instanceof Date) {
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
				value = sdf.format(value);
			}
			else {
				value = value.toString();
			}
		}
		return (String)value;
	}


	public void putProperty(String key, Object value) {
		if (value != null) {
			getProperties().put(key, value);
		}
	}


	public void putPropertyTimestamp(String key, Long value) {
		if (value != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			putProperty(key, Long.toString(value) + " (" + sdf.format(new Date(value)) + ")");
		}
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}

}
