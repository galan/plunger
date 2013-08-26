package de.galan.plunger.application;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import de.galan.plunger.command.CommandName;


/**
 * Defines the commandline arguments for the basic usage, as well as for the specific commands (additiv).
 * 
 * @author daniel
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
		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("Print program usage");
		Option optionHelp = OptionBuilder.create("h");

		OptionBuilder.withLongOpt("command");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("The command to execute against the target");
		Option optionCommand = OptionBuilder.create("C");

		OptionBuilder.withDescription("Highlights the output (set to true/false)");
		OptionBuilder.hasArg();
		Option optionColors = OptionBuilder.create("colors");

		OptionBuilder.withLongOpt("verbose");
		OptionBuilder.withDescription("Verbose mode. Causes plunger to print debugging messages about its progress.");
		Option optionVerbose = OptionBuilder.create("v");

		OptionBuilder.withDescription("Version");
		Option optionVersion = OptionBuilder.create("version");

		return createOptions(optionCommand, optionColors, optionHelp, optionVerbose, optionVersion);
	}


	protected Options createOptionsLs() {
		OptionBuilder.withLongOpt("consumer");
		OptionBuilder.withDescription("Only show destinations with consumers");
		Option optionSelector = OptionBuilder.create("c");

		OptionBuilder.withLongOpt("informations");
		OptionBuilder.withDescription("When set, additional informations (like counters) are omitted.");
		Option optionInfos = OptionBuilder.create("i");

		OptionBuilder.withLongOpt("messages");
		OptionBuilder.withDescription("Only show destinations with messages.");
		Option optionMessages = OptionBuilder.create("m");

		OptionBuilder.withLongOpt("persistent");
		OptionBuilder.withDescription("Filters temporary destinations.");
		Option optionPersistent = OptionBuilder.create("p");

		OptionBuilder.withLongOpt("temporary");
		OptionBuilder.withDescription("Filters persistent (durable) destinations.");
		Option optionTemp = OptionBuilder.create("t");

		return createOptions(optionSelector, optionInfos, optionMessages, optionPersistent, optionTemp);
	}


	protected Options createOptionsCat() {
		OptionBuilder.withLongOpt("body");
		OptionBuilder.withDescription("When set, the body will be omitted");
		Option optionBody = OptionBuilder.create("b");

		OptionBuilder.withLongOpt("cut");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Cuts the body after n characters, adding ... when characters were removed.");
		Option optionCut = OptionBuilder.create("c");
		//.withType(Long.class)

		OptionBuilder.withLongOpt("escape");
		OptionBuilder.withDescription("Escapes the message.\nWhen the output is intended for further processing, this switch will map all output to single line. JMS-properties are formatted as json, the body is escaped as well. This form is required for put.");
		Option optionEscape = OptionBuilder.create("e");

		OptionBuilder.withLongOpt("limit");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Limits the messages to the first n elements in a queue or received by a topic");
		//.withType(Long.class)

		Option optionLimit = OptionBuilder.create("n");
		OptionBuilder.withLongOpt("properties");
		OptionBuilder.withDescription("When set, the properties will be omitted");
		Option optionProperties = OptionBuilder.create("p");

		OptionBuilder.withLongOpt("remove");
		OptionBuilder.withDescription("Read messages will also be removed from the queue");
		Option optionRemove = OptionBuilder.create("r");

		OptionBuilder.withLongOpt("selector");
		OptionBuilder.hasArgs(); // is required, even we think of the selector query only as single argument
		OptionBuilder.withDescription("selector to filter the targets result");
		Option optionSelector = OptionBuilder.create("s");

		return createOptions(optionBody, optionCut, optionEscape, optionLimit, optionProperties, optionRemove, optionSelector);
	}


	protected Options createOptionsPut() {
		OptionBuilder.withLongOpt("file");
		OptionBuilder.hasArgs();
		OptionBuilder.withDescription("file with escaped messages (instead of stdin)");
		Option optionFile = OptionBuilder.create("f");

		OptionBuilder.withLongOpt("skip");
		OptionBuilder.withDescription("skip lines with errors");
		Option optionSkip = OptionBuilder.create("s");

		return createOptions(optionFile, optionSkip);
	}


	protected Options createOptionsCount() {
		OptionBuilder.withLongOpt("consumer");
		OptionBuilder.withDescription("Count consumers for destination (instead of messages)");
		Option optionSelector = OptionBuilder.create("c");

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
