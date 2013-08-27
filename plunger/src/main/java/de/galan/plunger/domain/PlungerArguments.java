package de.galan.plunger.domain;

import java.util.HashMap;
import java.util.Map;


/**
 * Container for the parsed arguments, given by the user on the commandline
 * 
 * @author daniel
 */
public class PlungerArguments {

	private Target target;
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
