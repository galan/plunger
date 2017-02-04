package de.galan.plunger.application;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.junit.Before;
import org.junit.Test;

import de.galan.plunger.command.CommandName;
import de.galan.plunger.config.Config;
import de.galan.plunger.config.Entry;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.domain.Target;


/**
 * CUT ArgumentMerger
 *
 * @author daniel
 */
public class ArgumentMergerTest {

	Config config;
	ArgumentMerger am;


	@Before
	public void before() {
		am = new ArgumentMerger();
		config = new Config();
	}


	@Test
	public void lsM() throws Exception {
		PlungerArguments pa = merge("provider://host:1234/queue.destination", CommandName.LS, "-m");
		assertMisc(pa, "ls", true, false);
		assertTarget(pa, "provider", "host", 1234, null, null, "queue.destination", "destination");

		assertFalse(pa.containsCommandArgument("c"));
		assertFalse(pa.containsCommandArgument("consumer"));
		assertTrue(pa.containsCommandArgument("m"));
		assertTrue(pa.containsCommandArgument("messages"));
	}


	@Test
	public void getByConfig() throws Exception {
		createConfigTarget("xyz", "provider", "host", 1234);
		PlungerArguments pa = merge("xyz/queue.destination?param=value", CommandName.LS, "-m");
		assertMisc(pa, "ls", true, false);
		assertTarget(pa, "provider", "host", 1234, null, null, "queue.destination", "destination");

		assertEquals(pa.getTarget().getParameterValue("param"), "value");
	}


	@Test
	public void mergeParameter() throws Exception {
		Target target = createConfigTarget("xyz", "provider", "host", 1234);
		target.getParameter().put("param", "xyz"); // will be overriden
		target.getParameter().put("ding", "dong"); // will be added
		PlungerArguments pa = merge("xyz/queue.destination?param=value&cmd=new", CommandName.LS, "-m");
		assertMisc(pa, "ls", true, false);
		assertTarget(pa, "provider", "host", 1234, null, null, "queue.destination", "destination");

		assertEquals(pa.getTarget().getParameterValue("param"), "value");
		assertEquals(pa.getTarget().getParameterValue("ding"), "dong");
		assertEquals(pa.getTarget().getParameterValue("cmd"), "new");
	}


	Target createConfigTarget(String alias, String provider, String host, Integer port) {
		Target target = new Target();
		target.setPort(port);
		target.setProvider(provider);
		target.setHost(host);
		Entry entry = new Entry(alias);
		entry.setTarget(target);
		config.getEntries().put(alias, entry);
		return target;
	}


	/**
	 * Updating to Apache commons leads to missing command arguments. Eg. the arguments '-n1 -re' were correctly
	 * interpreted by 1.2 with '-n1 -r -e', but since 1.3.1 the 'e' argument was missing!
	 */
	@Test
	public void catN1RE() throws Exception {
		PlungerArguments pa = merge("provider://host:1234/queue.destination", CommandName.CAT, "-n1", "-re");
		assertMisc(pa, "cat", true, false);
		assertTarget(pa, "provider", "host", 1234, null, null, "queue.destination", "destination");

		assertTrue(pa.containsCommandArgument("n"));
		assertTrue(pa.getCommandArgument("n").equals("1"));
		assertTrue(pa.containsCommandArgument("r"));
		assertTrue(pa.containsCommandArgument("e"));
	}


	protected PlungerArguments merge(String cmdTarget, CommandName name, String... args) throws Exception {
		Options options = new OptionsFactory().createOptions(name);
		List<String> arguments = new ArrayList<>();
		arguments.add("-C");
		arguments.add(name.toString().toLowerCase());
		arguments.addAll(Arrays.asList(args));
		CommandLine line = constructCommandLine(options, arguments.toArray(new String[] {}));
		return am.merge(cmdTarget, config, line, options);
	}


	protected CommandLine constructCommandLine(Options options, String... args) throws ParseException {
		CommandLineParser parser = new PosixParser();
		return parser.parse(options, args);
	}


	private void assertMisc(PlungerArguments pa, String command, boolean colors, boolean verbose) {
		assertNotNull(pa);
		assertEquals(command, pa.getCommand());
		assertEquals(colors, pa.isColors());
		assertEquals(verbose, pa.isVerbose());
	}


	protected void assertTarget(PlungerArguments pa, String provider, String host, Integer port, String username, String password, String destination, String shortDestination) {
		assertEquals(provider, pa.getTarget().getProvider());
		assertEquals(host, pa.getTarget().getHost());
		assertEquals(port, pa.getTarget().getPort());
		assertEquals(destination, pa.getTarget().getDestination());
		assertNull(pa.getTarget().getUsername());
		assertNull(pa.getTarget().getPassword());
		assertEquals(shortDestination, pa.getTarget().getShortDestination());
	}

}
