package de.galan.plunger.config;

/**
 * An entry in the plunger configuration file
 * 
 * @author daniel
 */
public class Entry {

	private String host;
	private String username;
	private String password;
	private String hostname;
	private Integer port;
	private String destination;
	private String selector;
	private Boolean colors;


	public Entry() {
	}


	public Entry(String host) {
		setHost(host);
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
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


	public String getHostname() {
		return hostname;
	}


	public void setHostname(String hostname) {
		this.hostname = hostname;
	}


	public Integer getPort() {
		return port;
	}


	public void setPort(Integer port) {
		this.port = port;
	}


	public String getDestination() {
		return destination;
	}


	public void setDestination(String destination) {
		this.destination = destination;
	}


	public String getSelector() {
		return selector;
	}


	public void setSelector(String selector) {
		this.selector = selector;
	}


	public Boolean isColors() {
		return colors;
	}


	public void setColors(Boolean colors) {
		this.colors = colors;
	}

}
