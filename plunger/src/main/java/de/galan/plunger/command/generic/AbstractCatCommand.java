package de.galan.plunger.command.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableLong;
import org.fusesource.jansi.Ansi.Color;

import de.galan.plunger.command.AbstractCommand;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.MessageMarshaller;
import de.galan.plunger.util.Output;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public abstract class AbstractCatCommand extends AbstractCommand {

	@Override
	public void process(PlungerArguments pa) throws CommandException {
		Message message = null;
		boolean firstMessage = true;
		MutableLong counter = new MutableLong();
		Long limit = pa.getCommandArgumentLong("n");
		beforeFirstMessage(pa);
		while(!isLimitExceeded(limit, counter) && (message = getNextMessage(pa)) != null) {
			firstMessage = printSeparator(firstMessage, pa);
			printMessage(pa, message);
		}
	}


	@SuppressWarnings("unused")
	protected void beforeFirstMessage(PlungerArguments pa) throws CommandException {
		// can be overriden
	}


	protected abstract Message getNextMessage(PlungerArguments pa) throws CommandException;


	protected boolean isLimitExceeded(Long limit, MutableLong counter) {
		boolean result = false;
		if (limit != null) {
			if (counter.longValue() >= limit) {
				result = true;
			}
			counter.increment();
		}
		return result;
	}


	protected boolean printSeparator(boolean firstMessage, PlungerArguments pa) {
		if (!firstMessage) {
			if (!pa.containsCommandArgument("e")) {
				Output.println(StringUtils.repeat("-", 64));
			}
		}
		return false;
	}


	protected void printMessage(PlungerArguments pa, Message message) {
		boolean excludeBody = pa.containsCommandArgument("b");
		if (excludeBody) {
			message.setBody(null);
		}
		else {
			Long cut = pa.getCommandArgumentLong("c"); // limiting the body output
			if (cut != null) {
				boolean addDots = StringUtils.length(message.getBody()) > cut;
				String cutted = StringUtils.substring(message.getBody(), 0, cut.intValue()) + (addDots ? "..." : "");
				message.setBody(cutted);
			}
		}

		if (pa.containsCommandArgument("e")) {
			String[] marshalled = new MessageMarshaller().marshalParts(message);
			Output.print(Color.GREEN, marshalled[0]);
			Output.print(marshalled[1]);
			Output.println(Color.YELLOW, marshalled[2]);
		}
		else {
			List<String> keys = new ArrayList<>(message.getProperties().keySet());
			Collections.sort(keys);
			for (String key: keys) {
				if (isSystemHeader(key)) {
					Output.print(Color.BLUE, key + ":");
				}
				else {
					Output.print(Color.GREEN, key + ":");
				}
				Output.println(" " + message.getPropertyString(key));
			}
			if (StringUtils.isNotBlank(message.getBody())) {
				Output.println(Color.YELLOW, message.getBody());
			}
		}
	}


	protected abstract boolean isSystemHeader(String headerName);

}
