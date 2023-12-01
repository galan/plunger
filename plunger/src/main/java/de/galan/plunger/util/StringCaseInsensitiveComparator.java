package de.galan.plunger.util;

import java.util.Comparator;


/**
 * Compares Strings independent of its case. Eg. sorting would result in aBcdEf instead of BEacdf.
 */
public class StringCaseInsensitiveComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		return o1.compareToIgnoreCase(o2);
	}

}
