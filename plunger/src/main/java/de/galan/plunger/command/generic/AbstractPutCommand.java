package de.galan.plunger.command.generic;

import org.fusesource.jansi.Ansi.Color;

import de.galan.plunger.command.AbstractCommand;
import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.FileMessageReader;
import de.galan.plunger.util.MessageMarshaller;
import de.galan.plunger.util.MessageReader;
import de.galan.plunger.util.Output;
import de.galan.plunger.util.SystemMessageReader;


/**
 * Provides generic put command, that already handles the plunger arguments.
 * 
 * @author daniel
 */
public abstract class AbstractPutCommand extends AbstractCommand {

	@Override
	protected void process(PlungerArguments pa) throws CommandException {
		MessageReader reader = getMessageReader(pa);

		long lineCount = 0;
		MessageMarshaller mm = new MessageMarshaller();
		String line = null;
		while((line = reader.read()) != null) {
			lineCount++;
			try {
				Message msg = mm.unmarshal(line);
				logMessage(pa, msg);
				sendMessage(pa, msg, lineCount);
			}
			catch (CommandException cex) {
				if (pa.isVerbose()) {
					Output.error("line could not be send: [" + line + "]");
				}
				if (!pa.containsCommandArgument("s")) {
					throw cex;
				}
			}
		}
	}


	private MessageReader getMessageReader(PlungerArguments pa) throws CommandException {
		try {
			return pa.containsCommandArgument("f") ? new FileMessageReader(pa.getCommandArgument("f")) : new SystemMessageReader();
		}
		catch (Exception ex) {
			throw new CommandException("Messages could not be read", ex);
		}
	}


	protected abstract void sendMessage(PlungerArguments pa, Message message, long count) throws CommandException;


	protected void logMessage(PlungerArguments pa, Message msg) {
		if (pa.isVerbose()) {
			String[] marshalled = new MessageMarshaller().marshalParts(msg);
			Output.print(Color.GREEN, marshalled[0]);
			Output.print(marshalled[1]);
			Output.println(Color.YELLOW, marshalled[2]);
		}
	}

}
