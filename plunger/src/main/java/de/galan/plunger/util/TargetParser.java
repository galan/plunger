package de.galan.plunger.util;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import de.galan.plunger.domain.Target;


/**
 * Parses a given target in form [username[:password]@]host[:port]
 * 
 * @author daniel
 */
public class TargetParser {

	private static final Pattern VALID_PATTERN = Pattern.compile("^([\\w-+]+(:[^@:]+)?@)?[0-9a-zA-Z]+([.][0-9a-zA-Z]+)*([:][0-9]+)?$");


	public Target parse(String proxy, int defaultPort) throws Exception {
		Target result = null;

		//URL url = new URL(proxy);
		//url.getProtocol();

		if (proxy != null && VALID_PATTERN.matcher(proxy).matches()) {
			int indexAuth = StringUtils.indexOf(proxy, "@");
			String username = null;
			String password = null;
			String host = null;
			if (indexAuth > 0) {
				String auth = StringUtils.substring(proxy, 0, indexAuth);
				host = StringUtils.substring(proxy, indexAuth + 1, proxy.length());
				String[] authSplit = StringUtils.split(auth, ":", 2);
				username = authSplit[0];
				if (authSplit.length == 2) {
					password = authSplit[1];
				}
			}
			else {
				host = proxy;
			}
			String[] hostSplit = StringUtils.split(host, ":", 2);
			String ip = hostSplit[0];
			int port = defaultPort;
			if (hostSplit.length == 2) {
				port = Integer.valueOf(hostSplit[1]);
			}
			result = new Target(null, username, password, ip, port);
		}

		return result;
	}

}
