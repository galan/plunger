package de.galan.plunger.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.galan.plunger.domain.Message;
import de.galan.plunger.util.MessageMarshaller;
import de.galan.plunger.util.Output;


/**
 * CUT MessageMarshaller
 * 
 * @author daniel
 */
public class MessageMarshallerTest {

	private MessageMarshaller mm;


	@Before
	public void setup() {
		mm = new MessageMarshaller();
	}


	@Test
	public void empty() throws Exception {
		Message m = new Message();
		String marshal = mm.marshal(m);
		assertEquals("{}\t", marshal);
		Message m2 = mm.unmarshal(marshal);
		assertEquals("", m2.getBody());
		assertEquals(0, m2.getPropertiesSize());
	}


	@Test
	public void single() throws Exception {
		Message m = new Message();
		m.setBody("c");
		m.putProperty("a", "b");
		String marshal = mm.marshal(m);
		assertEquals("{\"a\":\"b\"}\tc", marshal);
		Message m2 = mm.unmarshal(marshal);
		assertEquals("c", m2.getBody());
		assertEquals("b", m2.getProperty("a"));
		assertEquals(1, m2.getPropertiesSize());
	}


	@Test
	public void marshall() throws Exception {
		String body = "hello world }{\t\näöüß€";
		String field4 = "äöü}{\n\tß €";
		Message m = new Message();
		m.setBody(body);
		m.putProperty("a", "a");
		m.putProperty("b", 123L);
		m.putProperty("c", true);
		m.putProperty("d", field4);
		// marshall
		String marshal = mm.marshal(m);
		Output.print(marshal);
		assertThat(marshal, containsString("\"a\":\"a\""));
		assertThat(marshal, containsString("\"b\":123"));
		assertThat(marshal, containsString("\"c\":true"));
		assertThat(marshal, containsString("\"d\":\"äöü}{\\n\\tß \\u20AC\""));
		assertThat(marshal, endsWith("\thello world }{\\t\\näöüß€"));
		// unmarshall
		Message m2 = mm.unmarshal(marshal);
		assertEquals(body, m2.getBody());
		assertEquals(4, m2.getPropertiesSize());
		assertEquals("a", m2.getProperty("a"));
		assertEquals(123L, m2.getProperty("b"));
		assertEquals(true, m2.getProperty("c"));
		assertEquals(field4, m2.getProperty("d"));

	}

}
