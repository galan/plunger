package de.galan.plunger.domain;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class TargetTest {

	@Test
	public void all() throws Exception {
		Target t = new Target("hornetq://myuser:mypass@localhost:5445/jms.queue.testing");
		assertEquals("hornetq", t.getProvider());
		assertEquals("myuser", t.getUsername());
		assertEquals("mypass", t.getPassword());
		assertEquals("localhost", t.getHost());
		assertEquals("jms.queue.testing", t.getDestination());
		assertEquals(5445, t.getPort().intValue());
	}


	@Test
	public void withoutPassword() throws Exception {
		Target t = new Target("hornetq://myuser@localhost:5445/jms.queue.testing");
		assertEquals("hornetq", t.getProvider());
		assertEquals("myuser", t.getUsername());
		assertNull(t.getPassword());
		assertEquals("localhost", t.getHost());
		assertEquals("jms.queue.testing", t.getDestination());
		assertEquals(5445, t.getPort().intValue());
	}


	@Test
	public void withoutProvider() throws Exception {
		Target t = new Target("myuser:mypass@localhost:5445/jms.queue.testing");
		assertNull(t.getProvider());
		assertEquals("myuser", t.getUsername());
		assertEquals("mypass", t.getPassword());
		assertEquals("localhost", t.getHost());
		assertEquals("jms.queue.testing", t.getDestination());
		assertEquals(5445, t.getPort().intValue());
	}

}
