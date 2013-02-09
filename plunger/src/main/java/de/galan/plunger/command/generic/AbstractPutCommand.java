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
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public abstract class AbstractPutCommand extends AbstractCommand {

	@Override
	protected void process(PlungerArguments pa) throws CommandException {
		MessageReader reader = pa.containsCommandArgument("f") ? new FileMessageReader(pa.getCommandArgument("f")) : new SystemMessageReader();

		long lineCount = 0;
		MessageMarshaller mm = new MessageMarshaller();
		String line = null;
		while((line = reader.read()) != null) {
			lineCount++;
			Message msg = mm.unmarshal(line);
			logMessage(pa, msg);
			sendMessage(pa, msg, lineCount);
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
