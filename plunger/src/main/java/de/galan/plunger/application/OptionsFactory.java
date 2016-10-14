package de.galan.plunger.application;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.galan.plunger.command.CommandName;


/**
 * Defines the commandline arguments for the basic usage, as well as for the specific commands (additiv).
 */
public class OptionsFactory {

	/** Creates the basic plus the command specific options */
	public Options createOptions(CommandName command) {
		Options options = createBasicOptions();
		Options optionsCommand = createCommandOptions(command);
		appendOptions(options, optionsCommand);
		return options;
	}


	private void appendOptions(Options options, Options optionsToAppend) {
		if (options != null && optionsToAppend != null) {
			for (Object option: optionsToAppend.getOptions()) {
				options.addOption((Option)option);
			}
		}
	}


	/** Creates only the command specific options */
	public Options createCommandOptions(CommandName command) {
		Options result = null;
		if (command != null) {
			switch (command) {
				case LS:
					result = createOptionsLs();
					break;
				case CAT:
					result = createOptionsCat();
					break;
				case PUT:
					result = createOptionsPut();
					break;
				case COUNT:
					result = createOptionsCount();
					break;
			}
		}
		return result;
	}


	public Options createBasicOptions() {
		Option optionHelp = Option.builder("h").longOpt("help").desc("Print program usage").build();
		Option optionCommand = Option.builder("C").longOpt("command").desc("The command to execute against the target").hasArg().build();
		Option optionColors = Option.builder().longOpt("colors").desc("Highlights the output (set to true/false)").hasArg().build();
		String descVerbose = "Verbose mode. Causes plunger to print debugging messages about its progress.";
		Option optionVerbose = Option.builder("v").longOpt("verbose").desc(descVerbose).build();
		Option optionVersion = Option.builder().longOpt("version").desc("Version").build();
		return createOptions(optionCommand, optionColors, optionHelp, optionVerbose, optionVersion);
	}


	protected Options createOptionsLs() {
		Option optionSelector = Option.builder("c").longOpt("consumer").desc("Only show destinations with consumers").build();
		Option optionInfos = Option.builder("i").longOpt("informations").desc("When set, additional informations (like counters) are omitted.").build();
		Option optionMessages = Option.builder("m").longOpt("messages").desc("Only show destinations with messages.").build();
		Option optionPersistent = Option.builder("p").longOpt("persistent").desc("Filters temporary destinations.").build();
		Option optionTemp = Option.builder("t").longOpt("temporary").desc("Filters persistent (durable) destinations.").build();
		return createOptions(optionSelector, optionInfos, optionMessages, optionPersistent, optionTemp);
	}


	protected Options createOptionsCat() {
		Option optionBody = Option.builder("b").longOpt("body").desc("Suppresses the body").build();
		String descCut = "Cuts the body after n characters, adding ... when characters were removed.";
		Option optionCut = Option.builder("c").longOpt("cut").desc(descCut).hasArg().build();
		String descEscape = "Escapes the message.\nWhen the output is intended for further processing, this switch will map all output to single line. JMS-properties are formatted as json, the body is escaped as well. This form is required for put.";
		Option optionEscape = Option.builder("e").longOpt("escape").desc(descEscape).build();
		String descLimit = "Limits the messages to the first n elements in a queue or received by a topic";
		Option optionLimit = Option.builder("n").longOpt("limit").desc(descLimit).hasArg().build();
		Option optionProperties = Option.builder("p").longOpt("properties").desc("Suppresses the properties").build();
		Option optionSeparator = Option.builder("d").longOpt("seperator").desc("Suppresses the delimiting separator").build();
		Option optionRemove = Option.builder("r").longOpt("remove").desc("Read messages will also be removed from the queue").build();
		Option optionSelector = Option.builder("s").longOpt("selector").desc("Selector to filter the targets result (JMS)").hasArgs().build(); // hasArgs is required, even we think of the selector query only as single argument
		return createOptions(optionBody, optionCut, optionEscape, optionLimit, optionProperties, optionSeparator, optionRemove, optionSelector);
	}


	protected Options createOptionsPut() {
		Option optionFile = Option.builder("f").longOpt("file").desc("File with escaped messages (instead of stdin)").hasArgs().build();
		Option optionSkip = Option.builder("s").longOpt("skip").desc("Skip lines with errors").build();
		Option optionTtl = Option.builder("t").longOpt("ttl").desc("Time to live, see documentation for format").hasArgs().build();
		Option optionPrio = Option.builder("p").longOpt("priority").desc("Priority").hasArgs().build();
		Option optionRk = Option.builder("r").longOpt("routingkey").desc("Routingkey (AMQP)").hasArg().build();
		return createOptions(optionFile, optionSkip, optionTtl, optionPrio, optionRk);
	}


	protected Options createOptionsCount() {
		Option optionSelector = Option.builder("c").longOpt("consumer").desc("Count consumers for destination (instead of messages)").build();
		return createOptions(optionSelector);
	}


	private Options createOptions(Option... options) {
		Options result = new Options();
		for (Option option: options) {
			result.addOption(option);
		}
		return result;
	}

}
