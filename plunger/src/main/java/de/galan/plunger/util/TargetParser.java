package de.galan.plunger.util;

import java.net.URI;

import org.apache.commons.lang.StringUtils;

import de.galan.plunger.domain.Target;


/**
 * Parses a given target in form provider://[username[:password]@]host[:port]
 * 
 * @author daniel
 */
public class TargetParser {

	public Target parse(String target, int defaultPort) throws Exception {
		Target result = null;
		try {
			URI uri = new URI(target);
			if (StringUtils.isBlank(uri.getScheme()) || !StringUtils.contains(target, "://")) {
				throw new Exception("No provider given");
			}
			int port = uri.getPort();
			if (port == -1) {
				port = defaultPort;
			}

			String username = null;
			String password = null;
			if (StringUtils.isNotEmpty(uri.getUserInfo())) {
				String[] authSplit = StringUtils.split(uri.getUserInfo(), ":", 2);
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
