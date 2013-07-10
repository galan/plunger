package de.galan.plunger.application;

import static org.apache.commons.lang.StringUtils.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.config.Config;
import de.galan.plunger.config.Entry;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.domain.Target;


/**
 * Merges the possible stored configuration entry with the arguments from the commandline.
 * 
 * @author daniel
 */
public class ArgumentMerger {

	public PlungerArguments merge(String cmdTarget, Config config, CommandLine line, Options options) throws Exception {
		PlungerArguments result = new PlungerArguments();
		Target commandTarget = new Target(cmdTarget);
		Entry entry = config.getEntry(commandTarget.getHost());
		if ((entry != null) && (entry.getTarget() != null)) {
			result.setTarget(mergeTarget(commandTarget, entry.getTarget())); // merge targets
		}
		else {
			result.setTarget(commandTarget);
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


	protected boolean mergeColors(CommandLine line, PlungerArguments pa, Entry entry) {
		boolean result = true;
		if (entry != null && !entry.isColors()) {
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

}
