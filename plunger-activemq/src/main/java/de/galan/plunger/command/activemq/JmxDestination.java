package de.galan.plunger.command.activemq;

import static org.apache.commons.lang3.StringUtils.*;

import org.apache.commons.lang3.ObjectUtils;


/**
 * ActiveMQ abstraction for a destination.
 *
 * @author daniel
 */
class JmxDestination implements Comparable<JmxDestination> {

	private String objectName;
	private String destinationType;
	private boolean temporary;
	private String displayName;


	public JmxDestination(String objectName, String destinationType, boolean temporary) {
		this.objectName = objectName;
		this.destinationType = destinationType;
		this.temporary = temporary;
		displayName = lowerCase(destinationType) + "." + objectName;
	}


	public String getObjectName() {
		return objectName;
	}


	public String getDestinationType() {
		return destinationType;
	}


	public boolean isTemporary() {
		return temporary;
	}


	public String getDisplayName() {
		return displayName;
	}


	@Override
	public int compareTo(JmxDestination o) {
		return ObjectUtils.compare(getDisplayName(), o.getDisplayName());
	}

}
