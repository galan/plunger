package de.galan.plunger.application;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.fusesource.jansi.AnsiConsole;

import de.galan.plunger.command.Commands;
import de.galan.plunger.config.Config;
import de.galan.plunger.config.Entry;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.domain.Target;
import de.galan.plunger.util.IgnoringPosixParser;
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

	private OptionsFactory factory = new OptionsFactory();


	public static void main(String[] args) {
		AnsiConsole.systemInstall();
		new Plunger().start(args);
	}


	public void start(String[] args) {
		Options options = factory.createBasicOptions();
		CommandLineParser ignoringParser = new IgnoringPosixParser(true);
		Commands command = null;
		try {
			CommandLine lineBasic = ignoringParser.parse(options, args);

			command = Commands.get(lineBasic.getOptionValue("command"));
			Options optionsCommand = factory.createOptions(command);
			CommandLineParser parser = new PosixParser();
			CommandLine line = parser.parse(optionsCommand, args);

			checkInformationSwitches(optionsCommand, line, command);

			PlungerArguments pa = new PlungerArguments();
			Config config = new Config();
			if (!config.parse(System.getProperty("user.home") + System.getProperty("file.separator") + ".plunger")) {
				System.exit(2);
			}
			Entry entry = config.getEntry(line.getArgs()[0]);
			mergeArguments(pa, entry, line, factory.createCommandOptions(command));
			new Client().process(pa);
		}
		catch (Exception ex) {
			printUsage(command, ex.getMessage(), 1);
		}
	}


	protected void checkInformationSwitches(Options options, CommandLine line, Commands command) throws ParseException {
		if (line.hasOption("version")) {
			printVersion();
		}
		if (line.hasOption("help")) {
			//TODO check if "command" options is given
			printUsage(command, null, 0);
		}
		if (line.getArgs().length == 0) {
			throw new ParseException("No target provided");
		}
		else if (line.getArgs().length > 1) {
			throw new ParseException("To many targets provided");
		}
	}


	protected void mergeArguments(PlungerArguments pa, Entry entry, CommandLine line, Options options) throws Exception {
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

		for (Object opt: options.getOptions()) {
			Option option = (Option)opt;
			if (line.hasOption(option.getOpt())) {
				pa.addCommandArgument(option.getOpt(), line.getOptionValue(option.getOpt()));
				pa.addCommandArgument(option.getLongOpt(), line.getOptionValue(option.getOpt()));
			}
		}

		//String[] commandArguments = line.getOptionValues("command");
		//commandArguments = Arrays.copyOfRange(commandArguments, 1, commandArguments.length);
		//pa.setCommandArguments(commandArguments);
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
	protected void printUsage(Commands command, String message, int status) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("plunger <target> [options]", message, factory.createBasicOptions(), null);
		if (command != null) {
			Output.println("\nCommand specific options:");
			PrintWriter writer = new PrintWriter(System./**/out);
			helpFormatter.printOptions(writer, HelpFormatter.DEFAULT_WIDTH, factory.createCommandOptions(command), HelpFormatter.DEFAULT_LEFT_PAD,
				HelpFormatter.DEFAULT_DESC_PAD);
			writer.flush();
		}
		Output.println("\nPlunger homepage: " + DOCUMENTATION_URL);

		System.exit(status);
	}

}
