package de.galan.plunger.command.activemq;

/*
 * import javax.jms.Connection; import javax.jms.ConnectionFactory; import javax.jms.Destination; import
 * javax.jms.JMSException; import javax.jms.Queue; import javax.jms.Session; import javax.jms.Topic;
 */

/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public abstract class AbstractActivemqJmsCommand {//extends AbstractCommand {

	/*
		private ConnectionFactory factory;
		private Connection connection;
		private Session session;
		private Destination destination;
	*/

	//@Override
	//protected void initialize(PlungerArguments pa) throws CommandException {
	/*
	try {
		factory = new ActiveMQConnectionFactory("tcp://" + jca.getTarget().getHost() + ":" + jca.getTarget().getPort());
		connection = factory.createConnection(jca.getTarget().getUsername(), jca.getTarget().getPassword());
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		if (StringUtils.startsWith(jca.getDestination(), "jms.queue.")) {
			destination = session.createQueue(jca.getShortDestination());
		}
		else if (StringUtils.startsWith(jca.getDestination(), "jms.topic.")) {
			destination = session.createTopic(jca.getShortDestination());
		}
	}
	catch (Exception ex) {
		throw new CommandException("Could not connect to server", ex);
	}
	*/
	///}

	/*
		protected boolean isQueue() {
			return Queue.class.isAssignableFrom(getDestination().getClass());
		}


		protected boolean isTopic() {
			return Topic.class.isAssignableFrom(getDestination().getClass());
		}
	*/

	//@Override
	//protected void close() {
	/*
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
	*/
	//}

	/*
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
	*/
}
