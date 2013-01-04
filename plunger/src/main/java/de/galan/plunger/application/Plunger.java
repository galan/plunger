package de.galan.plunger.application;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.fusesource.jansi.AnsiConsole;

import de.galan.plunger.config.Config;
import de.galan.plunger.config.Entry;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.domain.Target;
import de.galan.plunger.util.Output;
import de.galan.plunger.util.TargetParser;
import de.galan.plunger.util.VersionUtil;


/**
 * Shell client to a Messaging Provider
 * 
 * @author daniel
 */
public class Plunger {

	private static final int DEFAULT_PORT = 5445;
	private static final String DOCUMENTATION_URL = "https://github.com/d8bitr/plunger";


	public static void main(String[] args) {
		AnsiConsole.systemInstall();
		new Plunger().process(args);
	}


	public void process(String[] args) {
		Options options = createOptions();
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine line = parser.parse(options, args);
			PlungerArguments pa = new PlungerArguments();
			if (line.hasOption("help")) {
				printUsage(options, null, 0);
			}
			if (line.hasOption("version")) {
				printVersion();
			}
			if (line.getArgs().length == 0) {
				throw new ParseException("No target provided");
			}
			else if (line.getArgs().length > 1) {
				throw new ParseException("To many targets provided");
			}
			Config config = new Config();
			if (!config.parse(System.getProperty("user.home") + System.getProperty("file.separator") + ".plunger")) {
				System.exit(2);
			}
			Entry entry = config.getEntry(line.getArgs()[0]);
			mergeArguments(pa, line, entry);
			new Client().process(pa);
		}
		catch (Exception ex) {
			printUsage(options, ex.getMessage(), 1);
		}
	}


	protected void mergeArguments(PlungerArguments pa, CommandLine line, Entry entry) throws Exception {
		Target target = null;
		if (entry != null) {
			target = new Target("hornetq-2.2.x", entry.getUsername(), entry.getPassword(), entry.getHostname(), entry.getPort() == null ? DEFAULT_PORT
					: entry.getPort());
			pa.setDestination(entry.getDestination());
			pa.setSelector(entry.getSelector());
			pa.setColors(entry.isColors());
		}
		else {
			target = new TargetParser().parse(line.getArgs()[0], DEFAULT_PORT);
		}
		pa.setTarget(target); // either config or cli
		pa.setDestination(line.getOptionValue("destination")); // config can be overriden by cli
		pa.setSelector(StringUtils.join(line.getOptionValues("selector"), " ")); // config can be overriden by cli
		boolean colors = StringUtils.equals(line.getOptionValue("colors"), "false") ? false : true;
		pa.setColors(colors); // config can be overriden by cli
		Output.setColor(pa.isColors());

		pa.setVerbose(line.hasOption("verbose"));
		pa.setCommand(line.getOptionValue("command"));

		String[] commandArguments = line.getOptionValues("command");
		commandArguments = Arrays.copyOfRange(commandArguments, 1, commandArguments.length);
		pa.setCommandArguments(commandArguments);
	}


	@SuppressWarnings("static-access")
	protected Options createOptions() {
		//[[[
		Option optionHelp = OptionBuilder
		                    .withLongOpt("help")
		                    .withDescription("print program usage")
		                    .create("h");
		Option optionCommand = OptionBuilder
		                       .withLongOpt("command")
		                       .hasArgs()
		                       .withDescription("the command to execute against the target")
		                       .create("c");
		Option optionSelector = OptionBuilder
		                        .withLongOpt("selector")
		                        .hasArgs()
		                        .withDescription("selector to filter the targets result")
		                        .create("s");
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
		Options options = new Options();
		options.addOption(optionCommand);
		options.addOption(optionSelector);
		options.addOption(optionDestination);
		options.addOption(optionColors);
		options.addOption(optionHelp);
		options.addOption(optionVerbose);
		options.addOption(optionVersion);
		return options;
	}


	protected void printVersion() {
		try {
			new VersionUtil().printVersion();
			System.exit(0);
		}
		catch (Exception ex) {
			System.exit(1);
		}
	}


	/** Prints the usage and terminates the application */
	protected void printUsage(Options options, String message, int status) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("plunger <target> [options]", message, options, "Documentation: " + DOCUMENTATION_URL);
		System.exit(status);
	}

}
