package de.galan.plunger.application;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import de.galan.plunger.command.Commands;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class OptionsFactory {

	public Options createOptions(Commands command) {
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


	public Options createCommandOptions(Commands command) {
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


	@SuppressWarnings("static-access")
	public Options createBasicOptions() {
		//[[[
		Option optionHelp = OptionBuilder
                .withLongOpt("help")
                .withDescription("Print program usage")
                .create("h");
		/* TODO
		Option optionTarget = OptionBuilder
                .withLongOpt("target")
                .hasArg()
                .required
                .withDescription("the messaging target")
                .create("t");*/
		Option optionCommand = OptionBuilder
		        .withLongOpt("command")
		        .hasArg()
		        .withDescription("The command to execute against the target")
		        .create("C");
		Option optionDestination = OptionBuilder
		        .withLongOpt("destination")
		        .hasArg()
		        .withDescription("Selects the queue or topic,\nhas to start with 'jms.queue.' or 'jms.topic.'") //TODO remove prefix
		        .create("d");
		Option optionColors = OptionBuilder
		        .withDescription("Highlights the output")
		        .hasArg()
		        .create("colors"); //TODO
		Option optionVerbose = OptionBuilder
		        .withLongOpt("verbose")
		        .withDescription("Verbose mode. Causes plunger to print debugging messages about its progress.")
		        .create("v");
		Option optionVersion = OptionBuilder
		        .withDescription("Version")
		        .create("version");
		//]]]
		return createOptions(optionCommand, optionDestination, optionColors, optionHelp, optionVerbose, optionVersion);
	}


	@SuppressWarnings("static-access")
	protected Options createOptionsLs() {
		//[[[
		Option optionSelector = OptionBuilder
                .withLongOpt("consumer")
                .withDescription("Only show destinations with consumers")
                .create("c");
		Option optionInfos = OptionBuilder
                .withLongOpt("informations")
                .withDescription("When set, additional informations (like counters) are omitted.")
                .create("i");
		Option optionMessages = OptionBuilder
                .withLongOpt("messages")
                .withDescription("When set, only show destinations with messages.")
                .create("m");
		Option optionPersistent = OptionBuilder
                .withLongOpt("persistent")
                .withDescription("When set, filter persistent (durable) destinations.")
                .create("p");
		Option optionTemp = OptionBuilder
                .withLongOpt("temporary")
                .withDescription("When set, filter temporary destinations.")
                .create("t");
		//]]]
		return createOptions(optionSelector, optionInfos, optionMessages, optionPersistent, optionTemp);
	}


	@SuppressWarnings("static-access")
	protected Options createOptionsCat() {
		//[[[
		Option optionBody = OptionBuilder
                .withLongOpt("body")
                .withDescription("When set, the body will be omitted")
                .create("b");
		Option optionCut = OptionBuilder
                .withLongOpt("cut")
                .hasArg()
                //.withType(Long.class)
                .withDescription("Cuts the body after n characters, adding ... when characters were removed.")
                .create("c");
		Option optionEscape = OptionBuilder
                .withLongOpt("escape")
                .withDescription("Escapes the message.\nWhen the output is intended for further processing, this switch will map all output to single line. JMS-properties are formatted as json, the body is escaped as well. This form is required for put.")
                .create("e");
		Option optionLimit = OptionBuilder
                .withLongOpt("limit")
                .hasArg()
                //.withType(Long.class)
                .withDescription("Limits the messages to the first n elements in a queue or received by a topic")
                .create("n");
		Option optionProperties = OptionBuilder
                .withLongOpt("properties")
                .withDescription("When set, the properties will be omitted")
                .create("p");
		Option optionRemove = OptionBuilder
                .withLongOpt("remove")
                .withDescription("Read messages will also be removed from the queue")
                .create("r");
		Option optionSelector = OptionBuilder
                .withLongOpt("selector")
                .hasArgs() // is required, even we think of the selector query only as single argument
                .withDescription("selector to filter the targets result")
                .create("s");
		//]]]
		return createOptions(optionBody, optionCut, optionEscape, optionLimit, optionProperties, optionRemove, optionSelector);
	}


	@SuppressWarnings("static-access")
	protected Options createOptionsPut() {
		//[[[
		Option optionFile= OptionBuilder
                .withLongOpt("file")
                .hasArgs()
                .withDescription("file with escaped messages (instead of stdin)")
                .create("f");
		//]]]
		return createOptions(optionFile);
	}


	@SuppressWarnings("static-access")
	protected Options createOptionsCount() {
		//[[[
		Option optionSelector = OptionBuilder
                .withLongOpt("consumer")
                .withDescription("Count consumers for destination (instead of messages)")
                .create("c");
		//]]]
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
