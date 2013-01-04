package de.galan.plunger.domain;

import java.util.HashMap;
import java.util.Map;


/**
 * Simplified Pojo for a message
 * 
 * @author daniel
 */
public class Message {

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


	public void putProperty(String key, Object value) {
		if (value != null) {
			getProperties().put(key, value);
		}
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}

}
