package de.galan.plunger.application;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import de.galan.plunger.command.Command;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.CommandProvider;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Executes the request provided by the PlungerArguments.
 * 
 * @author daniel
 */
public class Client {

	public void process(PlungerArguments pa) {
		Command command = determineCommand(pa);
		if (command == null) {
			if (StringUtils.isBlank(pa.getCommand())) {
				Output.error("No command given");
			}
			else {
				Output.error("Command '" + pa.getCommand() + "' unrecognized");
			}
		}
		else {
			try {
				command.execute(pa);
			}
			catch (CommandException ex) {
				handleError(pa, ex);
			}
		}
	}


	protected void handleError(PlungerArguments jca, CommandException ex) {
		if (jca.isVerbose()) {
			Output.error(ex.getMessage() + System.getProperty("line.separator") + ExceptionUtils.getFullStackTrace(ex));
		}
		else {
			Throwable cause = ex.getCause();
			String causeMessage = (cause != null && StringUtils.isNotBlank(cause.getMessage())) ? ": " + cause.getMessage() : "";
			Output.error(ex.getMessage() + causeMessage);
		}
		System.exit(4);
	}


	protected static Command determineCommand(PlungerArguments pa) {
		Command cmd = null;
		CommandProvider provider = new CommandProviderServiceLocator().locate(CommandProvider.class, pa.getTarget().getProvider());
		if (provider == null) {
			Output.error("No provider for '" + pa.getTarget().getProvider() + "' found");
			//exit
		}
		else {
			switch (pa.getCommand()) {
				case "ls":
					cmd = provider.ls(pa);
					break;
				case "cat":
					cmd = provider.cat(pa);
					break;
				case "put":
					cmd = provider.put(pa);
					break;
				case "count":
					cmd = provider.count(pa);
					break;
				default:
					break;
			}
		}
		return cmd;
	}

}
