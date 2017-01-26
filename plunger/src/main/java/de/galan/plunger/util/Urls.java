package de.galan.plunger.util;

import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;


/**
 * Simplified utility to read from urls with basic authentication.
 *
 * @author daniel
 */
public class Urls {

	public static String read(String url, String username, String password) throws IOException {
		String result = null;
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection)new URL(url).openConnection();
			if (isNotBlank(username) && isNotBlank(password)) {
				String pair = username + ":" + password;
				String encodedAuthorization = trim(Base64.encodeBase64String(pair.getBytes(UTF_8)));
				connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
			}
			//connection.setConnectTimeout(..);
			//connection.setReadTimeout(..);
			connection.setRequestMethod("GET");
			InputStream stream = (connection.getResponseCode() >= 400) ? connection.getErrorStream() : connection.getInputStream();
			//int statusCode = connection.getResponseCode();
			result = IOUtils.toString(stream, UTF_8);
			stream.close();
		}
		catch (Exception ex) {
			throw new IOException(ex);
		}
		finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return result;
	}

}
