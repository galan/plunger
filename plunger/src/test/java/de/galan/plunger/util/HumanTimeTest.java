package de.galan.plunger.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;


/**
 * CUT HumanTime
 * 
 * @author daniel
 */
public class HumanTimeTest {

	@Test
	public void humanizeTime() {
		assertEquals("0ms", HumanTime.humanizeTime(0L));
		assertEquals("1s200ms", HumanTime.humanizeTime(1200L));
	}


	@Test
	public void dehumanizeTime() {
		assertThat(HumanTime.dehumanizeTime("0"), is(equalTo(0L)));
		assertNull(HumanTime.dehumanizeTime(""));
		assertNull(HumanTime.dehumanizeTime(null));
		assertNull(HumanTime.dehumanizeTime("4h30m200"));

		assertThat(HumanTime.dehumanizeTime("1200"), is(equalTo(1200L)));
		assertThat(HumanTime.dehumanizeTime("1200ms"), is(equalTo(1200L)));
		assertThat(HumanTime.dehumanizeTime("1s200ms"), is(equalTo(1200L)));
		assertThat(HumanTime.dehumanizeTime("1s 200ms"), is(equalTo(1200L)));
		assertThat(HumanTime.dehumanizeTime(" 1s    200ms "), is(equalTo(1200L)));

		assertThat(HumanTime.dehumanizeTime("4h30m200ms"), is(equalTo(16200200L)));
		assertThat(HumanTime.dehumanizeTime("4h 30m 200ms"), is(equalTo(16200200L)));
		assertThat(HumanTime.dehumanizeTime("4h  30m200ms"), is(equalTo(16200200L)));

		assertThat(HumanTime.dehumanizeTime("2w3d4h5m6s7ms"), is(equalTo(1483506007L)));

		assertThat(HumanTime.dehumanizeTime("4m"), is(equalTo(240000L)));
	}


	@Test
	public void dehumanizeTimeCache() {
		assertThat(HumanTime.dehumanizeTime("2w3d4h5m6s7ms"), is(equalTo(1483506007L)));
		assertThat(HumanTime.dehumanizeTime("2w3d4h5m6s7ms"), is(equalTo(1483506007L)));
	}

}
