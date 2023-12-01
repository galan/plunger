package de.galan.plunger.config;

import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;

import de.galan.plunger.domain.Target;
import de.galan.plunger.domain.TargetParser;
import de.galan.plunger.util.Output;


/**
 * Reads the configuration, located at ~/.plunger. The configurationfile syntax is based on the .ssh/config file.
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
		getEntries().clear();
		if ((file != null) && file.exists() && file.isFile()) {
			int lineCount = 0;
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8))) {
				String line = null;
				Entry entry = null;
				while((line = br.readLine()) != null) {
					lineCount++;
					SimpleEntry<String, String> se = parsePair(line);
					if (se != null) {
						if (equalsIgnoreCase("Alias", se.getKey())) {
							entry = new Entry();
							// Allow aliases, separated by a single whitespace. They will refer the same entry.
							String[] aliases = split(se.getValue(), " ");
							for (String alias: aliases) {
								getEntries().put(alias, entry);
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
							if (equalsIgnoreCase("Colors", se.getKey())) {
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
		String input = substringBefore(line, LINE_COMMENT);
		if (isNotBlank(input)) {
			int indexKey = indexOfAny(line, " \t");
			if (indexKey > 0) {
				String key = substring(line, 0, indexKey);
				String value = substring(line, indexKey, line.length());
				if (isNotBlank(value)) {
					result = new SimpleEntry<>(key.trim(), value.trim());
				}
			}
		}
		return result;
	}


	public Entry getEntry(String alias) {
		return getEntries().get(alias);
	}


	@VisibleForTesting
	public Map<String, Entry> getEntries() {
		return entries;
	}

}
