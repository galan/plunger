package de.galan.plunger.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Prints the Version
 * 
 * @author daniel
 */
public class VersionUtil {

	public void printVersion() throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream("META-INF/maven/de.galan/plunger/pom.properties");
		Properties pp = new Properties();
		try {
			pp.load(is);
			Output.println(pp.getProperty("artifactId") + " version \"" + pp.getProperty("version") + "\"");
		}
		catch (IOException ex) {
			Output.error("Could not read version information from jar");
			throw ex;
		}
	}

}
