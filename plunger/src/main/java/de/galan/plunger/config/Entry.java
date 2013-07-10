package de.galan.plunger.config;

import de.galan.plunger.domain.Target;


/**
 * An entry in the plunger configuration file
 * 
 * @author daniel
 */
public class Entry {

	private String alias;
	private Target target;
	private Boolean colors;


	public Entry() {
	}


	public Entry(String alias) {
		setAlias(alias);
	}


	public String getAlias() {
		return alias;
	}


	public void setAlias(String alias) {
		this.alias = alias;
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
