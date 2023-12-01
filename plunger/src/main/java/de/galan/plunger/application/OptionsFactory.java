package de.galan.plunger.application;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
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
		/*
		Option optionHelp = Option.builder("h").longOpt("help").desc("Print program usage").build();
		Option optionCommand = Option.builder("C").longOpt("command").desc("The command to execute against the target").hasArg().build();
		Option optionColors = Option.builder().longOpt("colors").desc("Highlights the output (set to true/false)").hasArg().build();
		String descVerbose = "Verbose mode. Causes plunger to print debugging messages about its progress.";
		Option optionVerbose = Option.builder("v").longOpt("verbose").desc(descVerbose).build();
		Option optionVersion = Option.builder().longOpt("version").desc("Version").build();
		*/
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

		/*
		Option optionSelector = Option.builder("c").longOpt("consumer").desc("Only show destinations with consumers").build();
		Option optionInfos = Option.builder("i").longOpt("informations").desc("When set, additional informations (like counters) are omitted.").build();
		Option optionMessages = Option.builder("m").longOpt("messages").desc("Only show destinations with messages.").build();
		Option optionPersistent = Option.builder("p").longOpt("persistent").desc("Filters temporary destinations.").build();
		Option optionTemp = Option.builder("t").longOpt("temporary").desc("Filters persistent (durable) destinations.").build();
		*/
		return createOptions(optionSelector, optionInfos, optionMessages, optionPersistent, optionTemp);
	}


	protected Options createOptionsCat() {
		OptionBuilder.withLongOpt("body");
		OptionBuilder.withDescription("Suppresses the body");
		Option optionBody = OptionBuilder.create("b");

		OptionBuilder.withLongOpt("cut");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Cuts the body after n characters, adding ... when characters were removed.");
		Option optionCut = OptionBuilder.create("c");
		//.withType(Long.class)

		OptionBuilder.withLongOpt("escape");
		OptionBuilder.withDescription(
			"Escapes the message.\nWhen the output is intended for further processing, this switch will map all output to single line. JMS-properties are formatted as json, the body is escaped as well. This form is required for put.");
		Option optionEscape = OptionBuilder.create("e");

		OptionBuilder.withLongOpt("limit");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Limits the messages to the first n elements in a queue or received by a topic");
		//.withType(Long.class)

		Option optionLimit = OptionBuilder.create("n");
		OptionBuilder.withLongOpt("properties");
		OptionBuilder.withDescription("Suppresses the properties");
		Option optionProperties = OptionBuilder.create("p");

		OptionBuilder.withLongOpt("seperator");
		OptionBuilder.withDescription("Suppresses the delimiting separator");
		Option optionSeparator = OptionBuilder.create("d");

		OptionBuilder.withLongOpt("remove");
		OptionBuilder.withDescription("Read messages will also be removed from the queue");
		Option optionRemove = OptionBuilder.create("r");

		OptionBuilder.withLongOpt("selector");
		OptionBuilder.hasArgs(); // is required, even we think of the selector query only as single argument
		OptionBuilder.withDescription("Selector to filter the targets result (JMS)");
		Option optionSelector = OptionBuilder.create("s");

		/*
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
		*/
		return createOptions(optionBody, optionCut, optionEscape, optionLimit, optionProperties, optionSeparator, optionRemove, optionSelector);
	}


	protected Options createOptionsPut() {
		OptionBuilder.withLongOpt("file");
		OptionBuilder.hasArgs();
		OptionBuilder.withDescription("File with escaped messages (instead of stdin)");
		Option optionFile = OptionBuilder.create("f");

		OptionBuilder.withLongOpt("skip");
		OptionBuilder.withDescription("Skip lines with errors");
		Option optionSkip = OptionBuilder.create("s");

		OptionBuilder.withLongOpt("ttl");
		OptionBuilder.hasArgs(); // is required, see selector
		OptionBuilder.withDescription("Time to live, see documentation for format");
		Option optionTtl = OptionBuilder.create("t");

		OptionBuilder.withLongOpt("priority");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Priority");
		Option optionPrio = OptionBuilder.create("p");

		OptionBuilder.withLongOpt("routingkey");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Routingkey (AMQP)");
		Option optionRk = OptionBuilder.create("r");

		OptionBuilder.withLongOpt("direct");
		OptionBuilder.withDescription("Pass line direct as message (each line, unescaped, without header)");
		Option optionD = OptionBuilder.create("d");

		OptionBuilder.withLongOpt("compression");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Compression type (kafka). Supported values: none, gzip, snappy (default), lz4, or zstd.");
		Option optionCompression = OptionBuilder.create("c");

		/*
		Option optionFile = Option.builder("f").longOpt("file").desc("File with escaped messages (instead of stdin)").hasArgs().build();
		Option optionSkip = Option.builder("s").longOpt("skip").desc("Skip lines with errors").build();
		Option optionTtl = Option.builder("t").longOpt("ttl").desc("Time to live, see documentation for format").hasArgs().build();
		Option optionPrio = Option.builder("p").longOpt("priority").desc("Priority").hasArgs().build();
		Option optionRk = Option.builder("r").longOpt("routingkey").desc("Routingkey (AMQP)").hasArg().build();
		*/
		return createOptions(optionFile, optionSkip, optionTtl, optionPrio, optionRk, optionD, optionCompression);
	}


	protected Options createOptionsCount() {
		OptionBuilder.withLongOpt("consumer");
		OptionBuilder.withDescription("Count consumers for destination (instead of messages)");
		Option optionSelector = OptionBuilder.create("c");
		/*
		Option optionSelector = Option.builder("c").longOpt("consumer").desc("Count consumers for destination (instead of messages)").build();
		*/
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
