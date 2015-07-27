package de.galan.plunger.command.generic;

import static org.apache.commons.lang3.StringUtils.*;

import org.fusesource.jansi.Ansi.Color;

import de.galan.plunger.command.AbstractCommand;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Generic ls command, that handles most of the plunger arguments already.
 *
 * @author daniel
 */
public abstract class AbstractLsCommand extends AbstractCommand {

	protected void printDestination(PlungerArguments pa, String name, long countConsumer, long countMessages, boolean persistent) {
		boolean filterTemp = pa.containsCommandArgument("t");
		boolean filterPersistent = pa.containsCommandArgument("p");

		boolean filterPassed = (persistent && !filterPersistent) || (!persistent && !filterTemp);
		boolean optionMessages = !(pa.containsCommandArgument("m") && (countMessages <= 0));
		boolean optionConsumer = !(pa.containsCommandArgument("c") && (countConsumer <= 0));
		if (filterPassed && optionMessages && optionConsumer) {
			Color destinationColor = startsWith(name, "queue.") ? Color.CYAN : Color.GREEN;
			Output.print(destinationColor, name);
			if (!pa.containsCommandArgument("i")) {
				if (!persistent) {
					Output.print(" (temporary)");
				}
				Output.print(" (");
				Output.print(Color.BLUE, "" + countConsumer);
				Output.print("/");
				Output.print(Color.MAGENTA, "" + countMessages);
				Output.print(")");
			}
			Output.println("");
		}
	}

}
