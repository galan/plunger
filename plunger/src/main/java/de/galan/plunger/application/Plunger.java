package de.galan.plunger.application;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.Logger;

import de.galan.plunger.command.CommandName;
import de.galan.plunger.config.Config;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.IgnoringPosixParser;
import de.galan.plunger.util.Output;
import de.galan.plunger.util.VersionUtil;


/**
 * Shell client to a Messaging Provider
 *
 * @author daniel
 */
public class Plunger {

	private static final String DOCUMENTATION_URL = "https://github.com/galan/plunger";

	private OptionsFactory factory = new OptionsFactory();


	public static void main(String[] args) {
		System.setProperty(Logger.ROOT_LOGGER_NAME, "ERROR");
		//System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "ERROR");
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

			Config config = loadConfig();
			PlungerArguments pa = new ArgumentMerger().merge(line.getArgs()[0], config, line, optionsCommand);
			Output.setColor(pa.isColors());

			new Client().process(pa);
		}
		catch (Exception ex) {
			String msg = defaultIfBlank(ex.getMessage(), ex.getClass().getName());
			printUsage(command, msg, 1);
		}
	}


	private Config loadConfig() {
		Config config = new Config();
		String fs = System.getProperty("file.separator");
		// consider XDG config first, which is %XDG_CONFIG_HOME/plunger/targets, otherwise $HOME/.plunger
		String xdgPath = defaultString(System.getenv("XDG_CONFIG_HOME"), System.getProperty("user.home") + fs + ".config");
		File xdgFile = new File(new File(xdgPath, "plunger"), "targets");
		boolean xdgExist = xdgFile.exists() && xdgFile.isFile();
		File configFile = xdgExist ? xdgFile : new File(System.getProperty("user.home"), ".plunger");

		if (!config.parse(configFile)) {
			System.exit(2);
		}
		return config;
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
