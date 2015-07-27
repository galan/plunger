package de.galan.plunger.util;

import static org.apache.commons.lang3.StringUtils.*;


/**
 * Escapes and unescapes characters (some of <= 0x20) with a slash
 *
 * @author daniel
 */
public class Escape {

	private static final String[] REPLACE = new String[] {"\\", "\n", "\r", "\t", "\b", "\f", "\'", "\""};
	private static final String[] WITH = new String[] {"\\\\", "\\n", "\\r", "\\t", "\\b", "\\f", "\\'", "\\\""};


	public String escape(String text) {
		if (isNotBlank(text)) {
			return replaceEach(text, REPLACE, WITH);
		}
		return "";
	}


	public String unescape(String text) {
		if (isNotBlank(text)) {
			return replaceEach(text, WITH, REPLACE);
		}
		return "";
	}

}
