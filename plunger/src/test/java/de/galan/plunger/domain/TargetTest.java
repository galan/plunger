package de.galan.plunger.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


/**
 * CUT Target
 * 
 * @author daniel
 */
public class TargetTest {

	private Target target;


	@Before
	public void before() {
		target = new Target();
	}


	@Test
	public void destinationNull() throws Exception {
		target.setDestination(null);
		assertFalse(target.isDestinationSet());
		assertTrue(target.isDestinationErased());
	}


	@Test
	public void destinationSlash() throws Exception {
		target.setDestination("/");
		assertTrue(target.isDestinationSet());
		assertTrue(target.isDestinationErased());
	}


	@Test
	public void destinationSet() throws Exception {
		target.setDestination("queue.somewhere");
		assertTrue(target.isDestinationSet());
		assertFalse(target.isDestinationErased());
	}

}
