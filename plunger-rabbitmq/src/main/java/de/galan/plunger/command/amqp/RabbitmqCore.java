package de.galan.plunger.command.amqp;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Abstraction to deal with RabbitMQ API.
 *
 * @author daniel
 */
public class RabbitmqCore {

	private Connection connection;


	public void initialize(PlungerArguments pa) throws CommandException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(pa.getTarget().getUsername());
		factory.setPassword(pa.getTarget().getPassword());
		//factory.setVirtualHost(virtualHost);
		factory.setHost(pa.getTarget().getHost());
		factory.setPort(pa.getTarget().getPort());
		try {
			connection = factory.newConnection();
		}
		catch (IOException | TimeoutException ex) {
			throw new CommandException("Could not connect to server", ex);
		}
	}


	protected void close() {
		try {
			if (getConnection() != null) {
				getConnection().close();
			}
		}
		catch (IOException ioex) {
			Output.error("Failed to close connection: " + ioex.getMessage());
		}
	}


	public Connection getConnection() {
		return connection;
	}

}
