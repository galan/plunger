package de.galan.plunger.util;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * CUT Escape.
 *
 * @author daniel
 */
public class EscapeTest {

	Escape esc = new Escape();


	@Test
	public void escape() throws Exception {
		assertEquals("", esc.escape(null));
		assertEquals("", esc.escape(""));
		assertEquals("hallo", esc.escape("hallo"));
		assertEquals("hallo\\\\world", esc.escape("hallo\\world"));
		assertEquals("hallo\\nworld", esc.escape("hallo\nworld"));
		assertEquals("hallo\\rworld", esc.escape("hallo\rworld"));
		assertEquals("hallo\\r\\nworld", esc.escape("hallo\r\nworld"));
		assertEquals("hallo\\tworld\\t!", esc.escape("hallo\tworld	!"));
		assertEquals("hallo\\bworld", esc.escape("hallo\bworld"));
		assertEquals("hallo\\fworld", esc.escape("hallo\fworld"));
		assertEquals("hallo\\fworld", esc.escape("hallo\fworld"));
		assertEquals("hallo\\'world", esc.escape("hallo'world"));
		assertEquals("hallo\\\"world", esc.escape("hallo\"world"));
		assertEquals("hallo\\u2028world", esc.escape("hallo\u2028world"));
	}


	@Test
	public void unescape() throws Exception {
		assertEquals("", esc.unescape(null));
		assertEquals("", esc.unescape(""));
		assertEquals("hallo", esc.unescape("hallo"));
		assertEquals("hallo\\world", esc.unescape("hallo\\\\world"));
		assertEquals("hallo\nworld", esc.unescape("hallo\\nworld"));
		assertEquals("hallo\rworld", esc.unescape("hallo\\rworld"));
		assertEquals("hallo\r\nworld", esc.unescape("hallo\\r\\nworld"));
		assertEquals("hallo\tworld	!", esc.unescape("hallo\\tworld\\t!"));
		assertEquals("hallo\bworld", esc.unescape("hallo\\bworld"));
		assertEquals("hallo\fworld", esc.unescape("hallo\\fworld"));
		assertEquals("hallo\fworld", esc.unescape("hallo\\fworld"));
		assertEquals("hallo'world", esc.unescape("hallo\\'world"));
		assertEquals("hallo\"world", esc.unescape("hallo\\\"world"));
		assertEquals("hallo\u2028world", esc.unescape("hallo\\u2028world"));
	}

}
