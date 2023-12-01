package de.galan.plunger.application;

import static org.apache.commons.lang3.StringUtils.*;

import org.apache.commons.lang3.exception.ExceptionUtils;

import de.galan.plunger.command.Command;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.CommandName;
import de.galan.plunger.command.CommandProvider;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Executes the request provided by the PlungerArguments.
 */
public class Client {

	public void process(PlungerArguments pa) {
		CommandProvider provider = determineProvider(pa);
		mergeProviderSpecificConfiguration(pa, provider);
		Command command = determineCommand(pa, provider);
		if (command == null) {
			if (isBlank(pa.getCommand())) {
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
				handleError(pa, command, ex);
			}
		}
	}


	protected void mergeProviderSpecificConfiguration(PlungerArguments pa, CommandProvider provider) {
		if (pa.getTarget().getPort() == null) {
			pa.getTarget().setPort(provider.getDefaultPort());
		}
	}


	protected CommandProvider determineProvider(PlungerArguments pa) {
		CommandProvider provider = new CommandProviderServiceLocator().locate(CommandProvider.class, pa.getTarget().getProvider());
		if (provider == null) {
			if (isBlank(pa.getTarget().getProvider())) {
				handleError(pa, null, new CommandException("No provider given, is your target written correctly?"));
			}
			else {
				handleError(pa, null, new CommandException("No provider for '" + pa.getTarget().getProvider() + "' found"));
			}
		}
		return provider;
	}


	protected Command determineCommand(PlungerArguments pa, CommandProvider provider) {
		CommandName commandName = CommandName.get(pa.getCommand());
		return provider.getCommand(commandName, pa);
	}


	protected void handleError(PlungerArguments pa, Command command, CommandException cex) {
		if (pa.isVerbose()) {
			Output.error(cex.getMessage() + System.getProperty("line.separator") + ExceptionUtils.getStackTrace(cex));
		}
		else {
			Throwable cause = cex.getCause();
			String causeMessage = (cause != null && isNotBlank(cause.getMessage())) ? ": " + cause.getMessage() : "";
			Output.error(cex.getMessage() + causeMessage);
		}
		if (command != null) {
			try {
				command.onError(cex);
			}
			catch (Exception ex) {
				Output.error("Cleanup after error failed: " + ex.getMessage());
			}
		}
		System.exit(4);
	}

}
