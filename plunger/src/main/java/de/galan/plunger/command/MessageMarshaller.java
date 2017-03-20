package de.galan.plunger.command;

import de.galan.plunger.domain.Message;


/**
 * Conversion of messages to strings and vice versa.
 */
public interface MessageMarshaller {

	String marshal(Message message);


	Message unmarshal(String line) throws Exception;

}
