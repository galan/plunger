package de.galan.plunger.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * Reads input from a file
 * 
 * @author daniel
 */
public class FileMessageReader implements MessageReader {

	private File file;
	private Scanner sc;


	public FileMessageReader(String inputFile) {
		file = new File(inputFile);
		try {
			sc = new Scanner(new FileInputStream(file));
		}
		catch (FileNotFoundException ex) {
			Output.error("errr: " + inputFile);
		}
	}


	@Override
	public String read() {
		String result = null;
		if (sc.hasNextLine()) {
			result = sc.nextLine();
		}
		else {
			sc.close();
		}
		return result;
	}

}
