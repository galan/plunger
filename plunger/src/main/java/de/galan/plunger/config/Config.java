package de.galan.plunger.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.lang.StringUtils;

import de.galan.plunger.util.Output;
import de.galan.plunger.util.PlungerCharsets;


/**
 * Reads the configuration.
 * 
 * @author daniel
 */
public class Config {

	private static final String LINE_COMMENT = "#";

	Map<String, Entry> entries = new HashMap<>();


	public boolean parse(String file) {
		return parse(new File(file));
	}


	public boolean parse(File file) {
		boolean result = true;
		entries.clear();
		if (file != null) {
			if (file.exists() && file.isFile()) {
				int lineCount = 0;
				try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), PlungerCharsets.UTF8))) {
					String line = null;
					Entry entry = null;
					while((line = br.readLine()) != null) {
						lineCount++;
						SimpleEntry<String, String> se = parsePair(line);
						if (se != null) {
							if (StringUtils.equalsIgnoreCase("Host", se.getKey())) {
								entry = new Entry(se.getValue());
								entries.put(se.getValue(), entry);
							}
							else if (entry != null) {
								if (StringUtils.equalsIgnoreCase("Hostname", se.getKey())) {
									entry.setHostname(se.getValue());
								}
								else if (StringUtils.equalsIgnoreCase("Port", se.getKey())) {
									entry.setPort(Integer.parseInt(se.getValue()));
								}
								else if (StringUtils.equalsIgnoreCase("Username", se.getKey())) {
									entry.setUsername(se.getValue());
								}
								else if (StringUtils.equalsIgnoreCase("Password", se.getKey())) {
									entry.setPassword(se.getValue());
								}
								else if (StringUtils.equalsIgnoreCase("Destination", se.getKey())) {
									entry.setDestination(se.getValue());
								}
								else if (StringUtils.equalsIgnoreCase("Selector", se.getKey())) {
									entry.setSelector(se.getValue());
								}
								else if (StringUtils.equalsIgnoreCase("Colors", se.getKey())) {
									entry.setColors("true".equalsIgnoreCase(se.getValue()));
								}
							}
							else {
								throw new Exception("No Host defined");
							}
						}
					}
				}
				catch (Exception ex) {
					result = false;
					Output.error("Error parsing file '" + file.getAbsolutePath() + "' in line " + lineCount + ": " + ex.getMessage());
				}
			}
		}
		return result;
	}


	protected SimpleEntry<String, String> parsePair(String line) {
		SimpleEntry<String, String> result = null;
		String input = StringUtils.substringBefore(line, LINE_COMMENT);
		if (StringUtils.isNotBlank(input)) {
			int indexKey = StringUtils.indexOfAny(line, " \t");
			if (indexKey > 0) {
				String key = StringUtils.substring(line, 0, indexKey);
				String value = StringUtils.substring(line, indexKey, line.length());
				if (StringUtils.isNotBlank(value)) {
					result = new SimpleEntry<>(key.trim(), value.trim());
				}
			}
		}
		return result;
	}


	public Entry getEntry(String host) {
		return entries.get(host);
	}

}
