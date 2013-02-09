package de.galan.plunger.domain;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * Container for the parsed arguments, given by the user on the commandline
 * 
 * @author daniel
 */
public class PlungerArguments {

	private Target target;
	private String destination;
	private String selector;
	private String command;
	private Map<String, String> commandArguments;
	private boolean verbose;
	private Boolean colors;


	public Target getTarget() {
		return target;
	}


	public void setTarget(Target target) {
		this.target = target;
	}


	public String getDestination() {
		return destination;
	}


	public String getShortDestination() {
		String result = StringUtils.removeStart(getDestination(), "jms.queue.");
		result = StringUtils.removeStart(result, "jms.topic.");
		return result;
	}


	public void setDestination(String destination) {
		if (destination != null) {
			this.destination = destination;
		}
	}


	public String getCommand() {
		return command;
	}


	public void setCommand(String command) {
		this.command = command;
	}


	public void addCommandArgument(String key, String value) {
		getCommandArguments().put(key, value);
	}


	protected Map<String, String> getCommandArguments() {
		if (commandArguments == null) {
			commandArguments = new HashMap<String, String>();
		}
		return commandArguments;
	}


	public boolean containsCommandArgument(String argument) {
		return getCommandArguments().containsKey(argument);
	}


	public String getCommandArgument(String argument) {
		return getCommandArguments().get(argument);
	}


	public Long getCommandArgumentLong(String argument) {
		String ca = getCommandArguments().get(argument);
		return ca == null ? null : Long.valueOf(ca);
	}


	/*
	public String getCommandArgumentMatching(String pattern) {
		if (getCommandArguments() != null) {
			for (String arg: getCommandArguments()) {
				if (arg.matches(pattern)) {
					return arg;
				}
			}
		}
		return null;
	}
	*/

	public boolean isVerbose() {
		return verbose;
	}


	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}


	public boolean isColors() {
		return colors == null ? true : colors.booleanValue();
	}


	public void setColors(Boolean colors) {
		if (colors != null) {
			this.colors = colors;
		}
	}

}
