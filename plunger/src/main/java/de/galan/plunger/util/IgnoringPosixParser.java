package de.galan.plunger.util;

import java.util.ListIterator;

import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


/**
 * Apache commons cli doesn't support ignoring unknown options.
 * 
 * @author daniel
 */
public class IgnoringPosixParser extends PosixParser {

	private boolean ignoreUnrecognizedOption;


	public IgnoringPosixParser(final boolean ignoreUnrecognizedOption) {
		this.ignoreUnrecognizedOption = ignoreUnrecognizedOption;
	}


	@Override
	protected void processOption(final String arg, @SuppressWarnings("rawtypes") final ListIterator iter) throws ParseException {
		boolean hasOption = getOptions().hasOption(arg);

		if (hasOption || !ignoreUnrecognizedOption) {
			super.processOption(arg, iter);
		}
	}

}
