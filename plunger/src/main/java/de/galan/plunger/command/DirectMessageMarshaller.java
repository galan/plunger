package de.galan.plunger.command;

import de.galan.plunger.domain.Message;


/**
 * Reads Messages using no header and escaping, assuming each line is a message.
 */
public class DirectMessageMarshaller implements MessageMarshaller {

	@Override
	public String marshal(Message message) {
		throw new UnsupportedOperationException("MessageMarshaller does not support marshal(msg) mwthod");
	}


	@Override
	public Message unmarshal(String line) throws Exception {
		Message result = new Message();
		result.setBody(line);
		return result;
	}

}
