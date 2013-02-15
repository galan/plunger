package de.galan.plunger.util;

import static org.junit.Assert.*;

import org.junit.Test;

import de.galan.plunger.domain.Target;


/**
 * CUT TargetParser
 * 
 * @author danielt
 */
public class TargetParserTest {

	TargetParser tp = new TargetParser();


	@Test
	public void testParse() throws Exception {
		assertValid("a://1.2.3.4", "a", "1.2.3.4", null, null, null);
		assertValid("b://1.2.3.4:8888", "b", "1.2.3.4", 8888, null, null);
		assertValid("c://1.2.3.4:123", "c", "1.2.3.4", 123, null, null);
		assertValid("d://uuu@1.2.3.4", "d", "1.2.3.4", null, "uuu", null);
		assertValid("e://uuu@1.2.3.4:123", "e", "1.2.3.4", 123, "uuu", null);
		assertValid("f://uuu:ppp@1.2.3.4", "f", "1.2.3.4", null, "uuu", "ppp");
		assertValid("g://uuu:ppp@1.2.3.4:123", "g", "1.2.3.4", 123, "uuu", "ppp");
		assertValid("abc-123://uuu:ppp@1.2.3.4:123", "abc-123", "1.2.3.4", 123, "uuu", "ppp");

		// missing protocol
		assertInvalid("1.2.3.4");
		assertInvalid("1.2.3.4:8888");
		assertInvalid("1.2.3.4:123");
		assertInvalid("uuu@1.2.3.4");
		assertInvalid("uuu@1.2.3.4:123");
		assertInvalid("uuu:ppp@1.2.3.4");
		assertInvalid("uuu:ppp@1.2.3.4:123");
		// invalid format
		assertInvalid("");
		assertInvalid("x://");
		assertInvalid("@1.2.3.4");
		assertInvalid("uuu:@1.2.3.4");
		assertInvalid(":ppp@1.2.3.4");
		assertInvalid("uuu@ppp@1.2.3.4");
		assertInvalid("uuu:ppp@1.2.3.4@12323");
		assertInvalid("uuu@ppp:1.2.3.4@12323");
	}


	protected void assertInvalid(String proxy) throws Exception {
		assertNull(tp.parse(proxy));
	}


	protected void assertValid(String proxy, String provider, String ip, Integer port, String username, String password) throws Exception {
		Target result = tp.parse(proxy);
		assertEquals(provider, result.getProvider());
		assertEquals(ip, result.getHost());
		assertEquals(port, result.getPort());
		assertEquals(username, result.getUsername());
		assertEquals(password, result.getPassword());
	}

}
