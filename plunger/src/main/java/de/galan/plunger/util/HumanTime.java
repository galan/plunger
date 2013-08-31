package de.galan.plunger.util;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;


/**
 * Utility class to handle human readable time periods.
 * 
 * @author daniel
 */
public class HumanTime {

	private static final Pattern PATTERN_HUMAN_TIME = Pattern.compile("^([0-9]+w)?[ ]*([0-9]+d)?[ ]*([0-9]+h)?[ ]*([0-9]+m[^s]{0})?[ ]*([0-9]+s)?[ ]*([0-9]+ms)?$");

	public static final long MS_MILLISECOND = 1L;
	public static final long MS_SECOND = 1000L;
	public static final long MS_MINUTE = MS_SECOND * 60L;
	public static final long MS_HOUR = MS_MINUTE * 60L;
	public static final long MS_DAY = MS_HOUR * 24L;
	public static final long MS_WEEK = MS_DAY * 7L;


	protected static String timeAgo(Date date, Date reference) {
		String result = "";
		if ((date != null) && (reference != null)) {
			long time = reference.getTime() - date.getTime();
			result = humanizeTime(time, " ");
		}
		return result;
	}


	public static String humanizeTime(long time) {
		return humanizeTime(time, "");
	}


	private static String humanizeTime(long time, String separator) {
		StringBuilder result = new StringBuilder();
		if (time == 0L) {
			result.append("0ms");
		}
		else {
			long timeLeft = time;
			timeLeft = appendUnit(timeLeft, separator, MS_DAY, "d", result);
			timeLeft = appendUnit(timeLeft, separator, MS_HOUR, "h", result);
			timeLeft = appendUnit(timeLeft, separator, MS_MINUTE, "m", result);
			timeLeft = appendUnit(timeLeft, separator, MS_SECOND, "s", result);
			timeLeft = appendUnit(timeLeft, separator, MS_MILLISECOND, "ms", result);
		}
		return result.toString().trim();
	}


	private static long appendUnit(long time, String separator, long unit, String text, StringBuilder buffer) {
		long result = time;
		if (time >= unit) {
			long hours = time / unit;
			buffer.append(hours);
			buffer.append(text);
			buffer.append(separator);
			result -= unit * hours;
		}
		return result;
	}


	public static Long dehumanizeTime(String time) {
		Long result = null;
		if (StringUtils.isNotBlank(time)) {
			String input = time.trim();
			if (NumberUtils.isDigits(input)) {
				result = NumberUtils.toLong(input);
			}
			else {
				Matcher matcher = PATTERN_HUMAN_TIME.matcher(input);
				if (matcher.matches()) {
					long sum = 0L;
					sum += dehumanizeUnit(matcher.group(1), MS_WEEK);
					sum += dehumanizeUnit(matcher.group(2), MS_DAY);
					sum += dehumanizeUnit(matcher.group(3), MS_HOUR);
					sum += dehumanizeUnit(matcher.group(4), MS_MINUTE);
					sum += dehumanizeUnit(matcher.group(5), MS_SECOND);
					sum += dehumanizeUnit(matcher.group(6), MS_MILLISECOND);
					result = sum;
				}
			}
		}
		return result;
	}


	private static long dehumanizeUnit(String group, long unit) {
		long result = 0L;
		if (StringUtils.isNotBlank(group)) {
			String longString = group.replaceAll("[a-z ]", "");
			result = Long.valueOf(longString) * unit;
		}
		return result;
	}

}
