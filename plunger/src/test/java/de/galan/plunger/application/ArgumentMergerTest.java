package de.galan.plunger.application;

import static org.junit.Assert.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.junit.Before;
import org.junit.Test;

import de.galan.plunger.command.CommandName;
import de.galan.plunger.config.Config;
import de.galan.plunger.domain.PlungerArguments;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class ArgumentMergerTest {

	Config config;


	@Before
	public void before() {
		config = new Config();
	}


	protected CommandLine constructCommandLine(Options options, String... args) throws ParseException {
		CommandLineParser parser = new PosixParser();
		return parser.parse(options, args);
	}


	@Test
	public void testName() throws Exception {
		Options options = new OptionsFactory().createOptions(CommandName.LS);
		CommandLine line = constructCommandLine(options, "-C", "ls", "-m");

		ArgumentMerger am = new ArgumentMerger();
		PlungerArguments pa = am.merge("provider://host:1234/jms.queue.destination", config, line, options);
		assertEquals("ls", pa.getCommand());

		assertEquals("provider", pa.getTarget().getProvider());
		assertEquals("host", pa.getTarget().getHost());
		assertEquals(1234, pa.getTarget().getPort().intValue());
		assertEquals("jms.queue.destination", pa.getTarget().getDestination());
		assertNull(pa.getTarget().getUsername());
		assertNull(pa.getTarget().getPassword());
		assertEquals("destination", pa.getTarget().getShortDestination());

		assertTrue(pa.isColors());
		assertFalse(pa.isVerbose());
		assertFalse(pa.containsCommandArgument("c"));
		assertFalse(pa.containsCommandArgument("consumer"));
		assertTrue(pa.containsCommandArgument("m"));
		assertTrue(pa.containsCommandArgument("messages"));
		assertNotNull(pa);
	}

}
