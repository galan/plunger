package de.galan.plunger.util;

import static org.apache.commons.lang.StringUtils.*;

import java.net.URI;

import de.galan.plunger.domain.Target;


/**
 * Parses a given target in form [provider://][username[:password]@]host[:port]
 * 
 * @author daniel
 */
public class TargetParser {

	public Target parse(String target) throws Exception {
		Target result = null;
		try {
			URI uri = new URI(target);
			if (isBlank(uri.getScheme()) || !contains(target, "://")) {
				throw new Exception("No provider given");
			}
			Integer port = null;
			if (uri.getPort() != -1) {
				port = uri.getPort();
			}

			String username = null;
			String password = null;
			if (isNotEmpty(uri.getUserInfo())) {
				String[] authSplit = split(uri.getUserInfo(), ":", 2);
				username = authSplit[0];
				if (authSplit.length == 2) {
					password = authSplit[1];
				}
			}
			result = new Target(uri.getScheme(), username, password, uri.getHost(), port);

		}
		catch (Exception ex) {
			result = null;
			Output.error(ex.getMessage());
		}

		return result;
	}

}
