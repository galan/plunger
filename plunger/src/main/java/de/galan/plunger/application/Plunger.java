package de.galan.plunger.application;

import static org.apache.commons.lang.StringUtils.*;

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

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.CommandName;
import de.galan.plunger.config.Config;
import de.galan.plunger.config.Entry;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.domain.Target;
import de.galan.plunger.util.IgnoringPosixParser;
import de.galan.plunger.util.Output;
import de.galan.plunger.util.VersionUtil;


/**
 * Shell client to a Messaging Provider
 * 
 * @author daniel
 */
public class Plunger {

	private static final String DOCUMENTATION_URL = "https://github.com/d8bitr/plunger";

	private OptionsFactory factory = new OptionsFactory();


	public static void main(String[] args) {
		AnsiConsole.systemInstall();
		new Plunger().start(args);
	}


	public void start(String[] args) {
		Options options = factory.createBasicOptions();
		CommandLineParser ignoringParser = new IgnoringPosixParser(true);
		CommandName command = null;
		try {
			CommandLine lineBasic = ignoringParser.parse(options, args);

			command = CommandName.get(lineBasic.getOptionValue("command"));
			Options optionsCommand = factory.createOptions(command);
			CommandLineParser parser = new PosixParser();
			CommandLine line = parser.parse(optionsCommand, args);

			checkInformationSwitches(optionsCommand, line, command);
			//PlungerArguments pa = new PlungerArguments();

			Config config = new Config();
			if (!config.parse(System.getProperty("user.home") + System.getProperty("file.separator") + ".plunger")) {
				System.exit(2);
			}
			PlungerArguments pa = determinePlungerArguments(line.getArgs()[0], config, line, optionsCommand);
			Output.setColor(pa.isColors());

			//Entry entry = config.getEntry(line.getArgs()[0]);
			//mergeArguments(pa, entry, line, factory.createCommandOptions(command));
			new Client().process(pa);
		}
		catch (Exception ex) {
			String msg = defaultIfBlank(ex.getMessage(), ex.getClass().getName());
			printUsage(command, msg, 1);
		}
	}


	protected PlungerArguments determinePlungerArguments(String cmdTarget, Config config, CommandLine line, Options options) throws Exception {
		PlungerArguments result = new PlungerArguments();
		Target commandTarget = new Target(cmdTarget);
		Entry entry = config.getEntry(commandTarget.getHost());
		if ((entry != null) && (entry.getTarget() != null)) {
			result.setTarget(mergeTarget(commandTarget, entry.getTarget())); // merge targets
		}

		result.setColors(mergeColors(line, result, entry));

		result.setVerbose(line.hasOption("verbose"));
		result.setCommand(line.getOptionValue("command"));

		for (Object opt: options.getOptions()) {
			Option option = (Option)opt;
			if (line.hasOption(option.getOpt())) {
				String value = join(line.getOptionValues(option.getOpt()), " ");
				result.addCommandArgument(option.getOpt(), value);
				result.addCommandArgument(option.getLongOpt(), value);
			}
		}

		return result;
	}


	private boolean mergeColors(CommandLine line, PlungerArguments pa, Entry entry) {
		boolean result = true;
		if (!entry.isColors()) {
			result = false;
		}
		if (StringUtils.equals(line.getOptionValue("colors"), "false")) {
			result = false;
		}
		return result;
	}


	protected Target mergeTarget(Target ct, Target et) throws Exception {
		Target result = new Target(et.toString());
		if (ct.hasProvider()) {
			result.setProvider(ct.getProvider());
		}
		if (ct.hasUsername()) {
			result.setUsername(ct.getUsername());
		}
		if (ct.hasPassword()) {
			result.setPassword(ct.getPassword());
		}
		if (ct.hasPort()) {
			result.setPort(ct.getPort());
		}
		if (ct.hasDestination()) {
			result.setDestination(ct.getDestination());
		}

		if (!result.hasDestination()) {
			throw new CommandException("No destination is set");
		}
		return result;
	}


	protected void checkInformationSwitches(Options options, CommandLine line, CommandName command) throws ParseException {
		if (line.hasOption("version")) {
			printVersion();
		}
		if (line.hasOption("help")) {
			printUsage(command, null, 0);
		}
		if (line.getArgs().length == 0) {
			throw new ParseException("No target provided");
		}
		else if (line.getArgs().length > 1) {
			throw new ParseException("To many targets provided");
		}
	}


	/*
	protected void mergeArguments(PlungerArguments pa, Entry entry, CommandLine line, Options options) throws Exception {
		Target target = null;
		if (entry != null) {
			target = new Target("hornetq-2.2", entry.getUsername(), entry.getPassword(), entry.getHostname(), entry.getPort());
			pa.setDestination(entry.getDestination());
			pa.setColors(entry.isColors());
		}
		else {
			target = new TargetParser().parse(line.getArgs()[0]);
		}
		pa.setTarget(target); // either config or cli
		pa.setDestination(line.getOptionValue("destination")); // config can be overriden by cli
		if (isBlank(pa.getDestination())) {
			throw new CommandException("No destination is set");
		}
		boolean colors = StringUtils.equals(line.getOptionValue("colors"), "false") ? false : true;
		pa.setColors(colors); // config can be overriden by cli
		Output.setColor(pa.isColors());

		pa.setVerbose(line.hasOption("verbose"));
		pa.setCommand(line.getOptionValue("command"));

		for (Object opt: options.getOptions()) {
			Option option = (Option)opt;
			if (line.hasOption(option.getOpt())) {
				String value = join(line.getOptionValues(option.getOpt()), " ");
				pa.addCommandArgument(option.getOpt(), value);
				pa.addCommandArgument(option.getLongOpt(), value);
			}
		}
	}
	*/

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
	protected void printUsage(CommandName command, String message, int status) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("plunger <target> [options]", message, factory.createBasicOptions(), null);
		if (command != null) {
			Options commandOptions = factory.createCommandOptions(command);
			Output.println("\nCommand specific options:");
			if (commandOptions.getOptions().isEmpty()) {
				Output.println(" (none)");
			}
			else {
				PrintWriter writer = new PrintWriter(System./**/out);
				helpFormatter.printOptions(writer, HelpFormatter.DEFAULT_WIDTH, commandOptions, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD);
				writer.flush();
			}
		}
		Output.println("\nPlunger homepage: " + DOCUMENTATION_URL);

		System.exit(status);
	}

}
