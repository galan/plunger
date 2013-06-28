package de.galan.plunger.config;

import de.galan.plunger.domain.Target;


/**
 * An entry in the plunger configuration file
 * 
 * @author daniel
 */
public class Entry {

	private String host;
	private Target target;
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


	public Target getTarget() {
		return target;
	}


	public void setTarget(Target target) {
		this.target = target;
	}


	public Boolean isColors() {
		return (colors == null) || colors;
	}


	public void setColors(Boolean colors) {
		this.colors = colors;
	}

}
