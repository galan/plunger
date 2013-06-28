package de.galan.plunger.domain;

import static org.apache.commons.lang.StringUtils.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;


/**
 * The target to query, which is basically an uri with the provider, host, port and authentication information.
 * 
 * @author daniel
 */
public class Target {

	private static final String DUMMY_PROTOCOL = "gopher";

	private String provider;
	private String username;
	private String password;
	private String host;
	private Integer port;
	private String destination;


	public Target(String uri) throws URISyntaxException {
		this(new URI(contains(uri, "://") ? uri : DUMMY_PROTOCOL + "://" + uri));
	}


	public Target(URI uri) {
		initialize(uri);
	}


	protected void initialize(URI uri) {
		setProvider(StringUtils.equals(uri.getScheme(), DUMMY_PROTOCOL) ? null : uri.getScheme());
		setHost(uri.getHost());
		setPort(uri.getPort() == -1 ? null : uri.getPort());
		String userInfo = uri.getUserInfo();
		if (isNotBlank(userInfo)) {
			if (contains(userInfo, ":")) {
				String[] split = userInfo.split(":");
				setUsername(split[0]);
				setPassword(split[1]);
			}
			else {
				setUsername(userInfo);
			}
		}
		if (isNotBlank(uri.getRawPath())) {
			setDestination(removeStart(uri.getRawPath(), "/"));
		}
	}


	public Target(String provider, String username, String password, String host, Integer port) {
		setProvider(provider);
		setUsername(username);
		setPassword(password);
		setHost(host);
		setPort(port);
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
		String result = removeStart(getDestination(), "jms.queue.");
		result = removeStart(result, "jms.topic.");
		return result;
	}


	public void setDestination(String destination) {
		this.destination = destination;
	}


	public boolean hasDestination() {
		return isNotBlank(getDestination());
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
			buffer.append("/");
			buffer.append(getDestination());
		}
		return buffer.toString();
	}

}
