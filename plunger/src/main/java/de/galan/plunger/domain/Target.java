package de.galan.plunger.domain;

import org.apache.commons.lang.StringUtils;


/**
 * The target to query, which is basically an uri with the provider, host, port and authentication information
 * 
 * @author daniel
 */
public class Target {

	private String provider;
	private String username;
	private String password;
	private String host;
	private Integer port;


	public Target(String provider, String username, String password, String host, Integer port) {
		this.provider = provider;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
	}


	public String getProvider() {
		return provider;
	}


	public void setProvider(String provider) {
		this.provider = provider;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
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


	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getProvider());
		buffer.append("://");
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
		return buffer.toString();
	}

}
