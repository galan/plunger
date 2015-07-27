package de.galan.plunger.application;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;

import de.galan.plunger.command.CommandProvider;
import de.galan.plunger.util.Output;


/** SPI locator. Simply iterates orver the available SPIs and takes the CommandProvider for the given providerName. */
public class CommandProviderServiceLocator {

	public CommandProvider locate(Class<? extends CommandProvider> clazz, String providerName) {
		Iterator<? extends CommandProvider> iterator = ServiceLoader.load(clazz).iterator();
		while(iterator.hasNext()) {
			try {
				CommandProvider cp = iterator.next();
				if (StringUtils.equals(providerName, cp.getName())) {
					return cp;
				}
			}
			catch (Error e) {
				Output.error(e.getMessage());
			}
		}
		return null;
	}

}
