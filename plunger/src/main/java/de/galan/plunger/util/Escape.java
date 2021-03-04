package de.galan.plunger.util;

import static org.apache.commons.lang3.StringUtils.*;


/**
 * Escapes and unescapes characters (some of &lt;= 0x20) with a slash
 *
 * @author daniel
 */
public class Escape {

	private static final String[] UNESCAPED = new String[] {"\\", "\n", "\r", "\t", "\b", "\f", "\'", "\"", "\u2028", "\u2029"};
	private static final String[] ESCAPED = new String[] {"\\\\", "\\n", "\\r", "\\t", "\\b", "\\f", "\\'", "\\\"", "\\u2028", "\\u2029"};

	public String escape(String text) {
		if (isNotBlank(text)) {
			return replaceEach(text, UNESCAPED, ESCAPED);
		}
		return EMPTY;
	}


	public String unescape(String text) {
		if (isNotBlank(text)) {
			return replaceEach(text, ESCAPED, UNESCAPED);
		}
		return EMPTY;
	}

}
