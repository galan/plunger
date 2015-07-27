package de.galan.plunger.util;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;

import de.galan.plunger.domain.Target;


/**
 * Parses a given target in form [provider://][username[:password]@]host[:port][/destination]
 *
 * @author daniel
 */
public class TargetParser {

	private static final String DUMMY_PROTOCOL = "gopher"; // satisfy javas URI parser


	public Target parse(String target) throws Exception {
		Target result = null;
		try {
			URI uri = new URI(contains(target, "://") ? target : DUMMY_PROTOCOL + "://" + target);
			result = new Target();
			result.setProvider(StringUtils.equals(uri.getScheme(), DUMMY_PROTOCOL) ? null : uri.getScheme());
			result.setHost(uri.getHost());
			result.setPort(uri.getPort() == -1 ? null : uri.getPort());
			String userInfo = uri.getUserInfo();
			if (isNotBlank(userInfo)) {
				if (contains(userInfo, ":")) {
					String[] split = userInfo.split(":");
					result.setUsername(split[0]);
					result.setPassword(split[1]);
				}
				else {
					result.setUsername(userInfo);
				}
			}
			if (isNotBlank(uri.getRawPath())) {
				result.setDestination(length(uri.getRawPath()) > 1 ? removeStart(uri.getRawPath(), "/") : uri.getRawPath());
			}
			//query string
			if (isNotBlank(uri.getQuery())) {
				for (String pair: StringUtils.split(uri.getQuery(), "&")) {
					String[] split = StringUtils.split(pair, "=", 2);
					if (split.length > 0) {
						String key = split[0];
						if (isNotBlank(key)) {
							String value = null;
							if (split.length > 1) {
								if (isNotBlank(split[1])) {
									try {
										value = URLDecoder.decode(split[1], PlungerCharsets.UTF8.toString());
									}
									catch (UnsupportedEncodingException ex) {
										Output.error("UTF-8 unknown");
									}
								}
							}
							result.getParameter().put(key, value);
						}
					}
				}
			}
			if (isBlank(result.getHost())) {
				result = null;
			}
		}
		catch (URISyntaxException uex) {
			// nada
		}
		return result;
	}

}
