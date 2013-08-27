package de.galan.plunger.config;

import static org.apache.commons.lang.StringUtils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.galan.plunger.domain.Target;
import de.galan.plunger.util.Output;
import de.galan.plunger.util.PlungerCharsets;
import de.galan.plunger.util.TargetParser;


/**
 * Reads the configuration, located at ~/.plunger. The configurationfile syntax is based on the .ssh/config file.
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
		TargetParser parser = new TargetParser();
		entries.clear();
		if ((file != null) && file.exists() && file.isFile()) {
			int lineCount = 0;
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), PlungerCharsets.UTF8))) {
				String line = null;
				Entry entry = null;
				while((line = br.readLine()) != null) {
					lineCount++;
					SimpleEntry<String, String> se = parsePair(line);
					if (se != null) {
						if (StringUtils.equalsIgnoreCase("Alias", se.getKey())) {
							entry = new Entry();
							// Allow aliases, separated by a single whitespace. They will refer the same entry.
							String[] aliases = StringUtils.split(se.getValue(), " ");
							for (String alias: aliases) {
								entries.put(alias, entry);
							}
						}
						else if (entry != null) {
							if (equalsIgnoreCase("Target", se.getKey())) {
								Target target = parser.parse(se.getValue());
								if (!target.hasProvider()) {
									throw new Exception("Target defined for '" + entry.getAlias() + "' requires a provider");
								}
								entry.setTarget(target);
							}
							if (StringUtils.equalsIgnoreCase("Colors", se.getKey())) {
								entry.setColors("true".equalsIgnoreCase(se.getValue()));
							}
						}
						else {
							throw new Exception("No alias defined");
						}
					}
				}
			}
			catch (Exception ex) {
				result = false;
				Output.error("Error parsing file '" + file.getAbsolutePath() + "' in line " + lineCount + ": " + ex.getMessage());
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


	public Entry getEntry(String alias) {
		return entries.get(alias);
	}

}
