package de.galan.plunger.util;

import static org.junit.Assert.*;

import org.junit.Test;

import de.galan.plunger.domain.Target;
import de.galan.plunger.util.TargetParser;


/**
 * CUT TargetParser
 * 
 * @author daniel
 */
public class TargetParserTest {

	TargetParser tp = new TargetParser();


	@Test
	public void testParse() throws Exception {
		assertValid("1.2.3.4", "1.2.3.4", 123, null, null);
		assertValid("1.2.3.4:8888", "1.2.3.4", 8888, null, null);
		assertValid("1.2.3.4:123", "1.2.3.4", 123, null, null);
		assertValid("uuu@1.2.3.4", "1.2.3.4", 123, "uuu", null);
		assertValid("uuu@1.2.3.4:123", "1.2.3.4", 123, "uuu", null);
		assertValid("uuu:ppp@1.2.3.4", "1.2.3.4", 123, "uuu", "ppp");
		assertValid("uuu:ppp@1.2.3.4:123", "1.2.3.4", 123, "uuu", "ppp");
		assertInvalid("");
		assertInvalid("@1.2.3.4");
		assertInvalid("uuu:@1.2.3.4");
		assertInvalid(":ppp@1.2.3.4");
		assertInvalid("uuu@ppp@1.2.3.4");
		assertInvalid("uuu:ppp@1.2.3.4@12323");
		assertInvalid("uuu@ppp:1.2.3.4@12323");
	}


	protected void assertInvalid(String proxy) throws Exception {
		assertNull(tp.parse(proxy, 123));
	}


	protected void assertValid(String proxy, String ip, Integer port, String username, String password) throws Exception {
		Target result = tp.parse(proxy, 123);
		assertEquals(ip, result.getHost());
		if (port == null) {
			assertEquals(123, result.getPort());
		}
		else {
			assertEquals(port, Integer.valueOf(result.getPort()));
		}
		assertEquals(username, result.getUsername());
		assertEquals(password, result.getPassword());
	}

}
