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
		                    .withDescription("print program usage")
		                    .create("h");
		/*
		Option optionTarget = OptionBuilder
                              .withLongOpt("target")
                              .hasArg()
                              .required
                              .withDescription("the messaging target")
                              .create("t");*/
		Option optionCommand = OptionBuilder
		                       .withLongOpt("command")
		                       .hasArg()
		                       .withDescription("the command to execute against the target")
		                       .create("c");
		Option optionDestination = OptionBuilder
		                           .withLongOpt("destination")
		                           .hasArg()
		                           .withDescription("selects the queue or topic,\nhas to start with 'jms.queue.' or 'jms.topic.'")
		                           .create("d");
		Option optionColors = OptionBuilder
		                      .withDescription("highlights the output")
		                      .hasArg()
		                      .create("colors");
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


	//@SuppressWarnings("static-access")
	protected Options createOptionsLs() {
		//[[[
		//]]]
		return createOptions();
	}


	@SuppressWarnings("static-access")
	protected Options createOptionsCat() {
		//[[[
		Option optionSelector = OptionBuilder
		                        .withLongOpt("selector")
		                        .hasArgs()
		                        .withDescription("selector to filter the targets result")
		                        .create("s");
		//]]]
		return createOptions(optionSelector);
	}


	//@SuppressWarnings("static-access")
	protected Options createOptionsPut() {
		//[[[
		//]]]
		return createOptions();
	}


	@SuppressWarnings("static-access")
	protected Options createOptionsCount() {
		//[[[
		Option optionSelector = OptionBuilder //TODO required here?
		                        .withLongOpt("selector")
		                        .hasArgs()
		                        .withDescription("selector to filter the targets result")
		                        .create("s");
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
