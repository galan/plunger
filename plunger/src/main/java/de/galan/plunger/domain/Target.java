package de.galan.plunger.domain;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import de.galan.plunger.util.Output;
import de.galan.plunger.util.PlungerCharsets;


/**
 * The target to query, which is basically an uri with the provider, host, port and authentication information.
 */
public class Target {

	private String provider;
	private String username;
	private String password;
	private String host;
	private Integer port;
	private String destination;
	private Map<String, String> parameter = new TreeMap<>();


	public Target() {
		// nada
	}


	public String getProvider() {
		return provider;
	}


	public void setProvider(String provider) {
		this.provider = provider;
	}


	public boolean hasProvider() {
		return isNotBlank(getProvider());
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public boolean hasUsername() {
		return isNotBlank(getUsername());
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public boolean hasPassword() {
		return isNotBlank(getPassword());
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}


	public Integer getPort() {
		return port;
	}


	public void setPort(Integer port) {
		this.port = port;
	}


	public boolean hasPort() {
		return getPort() != null;
	}


	public String getDestination() {
		return destination;
	}


	public String getShortDestination() {
		String result = removeStart(getDestination(), "queue.");
		return removeStart(result, "topic.");
	}


	public void setDestination(String destination) {
		this.destination = destination;
	}


	public boolean isDestinationSet() {
		return isNotBlank(getDestination());
	}


	public boolean isDestinationErased() {
		return isBlank(getDestination()) || getDestination().equals("/");
	}


	public Map<String, String> getParameter() {
		return parameter;
	}


	public String getParameterValue(String key) {
		return getParameter().get(key);
	}


	public boolean containsParameter(String key) {
		return getParameter().keySet().contains(key);
	}


	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if (isNotBlank(getProvider())) {
			buffer.append(getProvider());
			buffer.append("://");
		}
		if (StringUtils.isNotBlank(getUsername())) {
			buffer.append(getUsername());
			if (getPassword() != null) {
				buffer.append(":");
				buffer.append(getPassword());
			}
			buffer.append("@");
		}
		buffer.append(getHost());
		if (getPort() != null) {
			buffer.append(":");
			buffer.append(getPort());
		}
		if (isNotBlank(getDestination())) {
			if (!"/".equals(getDestination())) {
				buffer.append("/");
			}
			buffer.append(getDestination());
		}
		if (!getParameter().isEmpty()) {
			buffer.append("?");
			for (Entry<String, String> entry: getParameter().entrySet()) {
				buffer.append(entry.getKey());
				buffer.append("=");
				if (isNotBlank(entry.getValue())) {
					try {
						buffer.append(URLDecoder.decode(entry.getValue(), PlungerCharsets.UTF8.toString()));
					}
					catch (UnsupportedEncodingException ex) {
						Output.error("UTF-8 unknown");
					}
				}
			}
		}
		return buffer.toString();
	}
}
