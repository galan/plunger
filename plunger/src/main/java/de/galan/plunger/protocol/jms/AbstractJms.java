package de.galan.plunger.protocol.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.commons.lang.StringUtils;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Basic connectivity for JMS-providers
 * 
 * @author daniel
 */
public abstract class AbstractJms {

	private ConnectionFactory factory;
	private Connection connection;
	private Session session;
	private Destination destination;


	public void initialize(PlungerArguments pa) throws CommandException {
		try {
			factory = createConnectionFactory(pa);
			connection = factory.createConnection(pa.getTarget().getUsername(), pa.getTarget().getPassword());
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			if (StringUtils.startsWith(pa.getDestination(), "jms.queue.")) {
				destination = session.createQueue(pa.getShortDestination());
			}
			else if (StringUtils.startsWith(pa.getDestination(), "jms.topic.")) {
				destination = session.createTopic(pa.getShortDestination());
			}
		}
		catch (Exception ex) {
			throw new CommandException("Could not connect to server", ex);
		}
	}


	protected abstract ConnectionFactory createConnectionFactory(PlungerArguments pa);


	public boolean isQueue() {
		return Queue.class.isAssignableFrom(getDestination().getClass());
	}


	public boolean isTopic() {
		return Topic.class.isAssignableFrom(getDestination().getClass());
	}


	public void close() {
		try {
			if (getSession() != null) {
				getSession().close();
			}
			if (getConnection() != null) {
				getConnection().close();
			}
		}
		catch (JMSException jex) {
			Output.error("Failed to close connection: " + jex.getMessage());
		}
	}


	public Session getSession() {
		return session;
	}


	public ConnectionFactory getFactory() {
		return factory;
	}


	public Connection getConnection() {
		return connection;
	}


	public Destination getDestination() {
		return destination;
	}

}
