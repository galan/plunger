package de.galan.plunger.domain;

import org.apache.commons.lang.ArrayUtils;
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
	private String[] commandArguments;
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


	public String getSelector() {
		return selector;
	}


	public void setSelector(String selector) {
		if (selector != null) {
			this.selector = selector;
		}
	}


	public String getCommand() {
		return command;
	}


	public void setCommand(String command) {
		this.command = command;
	}


	public String[] getCommandArguments() {
		return commandArguments;
	}


	public void setCommandArguments(String... commandArguments) {
		this.commandArguments = commandArguments;
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


	public boolean hasCommandArgument(String argument) {
		if (getCommandArguments() == null) {
			return false;
		}
		return ArrayUtils.contains(getCommandArguments(), argument);
	}


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


	public Integer getCommandArgumentPrefixInteger(String prefix) {
		Integer result = null;
		String match = getCommandArgumentMatching("^" + prefix + "[0-9]+$");
		if (StringUtils.isNotBlank(match)) {
			result = Integer.valueOf(StringUtils.substring(match, 1, match.length()));
		}
		return result;
	}

}
