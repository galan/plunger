package de.galan.plunger.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.galan.plunger.util.TargetParser;


/**
 * CUT TargetParser/Target.
 * 
 * @author daniel
 */
public class TargetParserTest {

	private TargetParser parser;


	@Before
	public void before() {
		parser = new TargetParser();
	}


	@Test
	public void all() throws Exception {
		Target t = parser.parse("hornetq://myuser:mypass@localhost:5445/queue.testing");
		assertEquals("hornetq", t.getProvider());
		assertEquals("myuser", t.getUsername());
		assertEquals("mypass", t.getPassword());
		assertEquals("localhost", t.getHost());
		assertEquals("queue.testing", t.getDestination());
		assertEquals(5445, t.getPort().intValue());
	}


	@Test
	public void withoutPassword() throws Exception {
		Target t = parser.parse("hornetq://myuser@localhost:5445/queue.testing");
		assertEquals("hornetq", t.getProvider());
		assertEquals("myuser", t.getUsername());
		assertNull(t.getPassword());
		assertEquals("localhost", t.getHost());
		assertEquals("queue.testing", t.getDestination());
		assertEquals(5445, t.getPort().intValue());
	}


	@Test
	public void withoutProvider() throws Exception {
		Target t = parser.parse("myuser:mypass@localhost:5445/queue.testing");
		assertNull(t.getProvider());
		assertEquals("myuser", t.getUsername());
		assertEquals("mypass", t.getPassword());
		assertEquals("localhost", t.getHost());
		assertEquals("queue.testing", t.getDestination());
		assertEquals(5445, t.getPort().intValue());
	}


	@Test
	public void onlyHost() throws Exception {
		Target t = parser.parse("localhost");
		assertNull(t.getProvider());
		assertNull(t.getUsername());
		assertNull(t.getPassword());
		assertEquals("localhost", t.getHost());
		assertNull(t.getDestination());
		assertNull(t.getPort());
	}

}
