package de.galan.plunger.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Reads message from stdin
 * 
 * @author daniel
 */
public class SystemMessageReader implements MessageReader {

	private BufferedReader in;


	public SystemMessageReader() {
		InputStreamReader isr = new InputStreamReader(System.in);
		in = new BufferedReader(isr);
	}


	@Override
	public String read() {
		try {
			return in.readLine();
		}
		catch (IOException ex) {
			return null;
		}
	}

}
