package de.galan.plunger.command.hornetq2;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class HornetqCore {

	private ClientSession session;
	private ClientSessionFactory factory;
	private ServerLocator locator;


	public void initialize(PlungerArguments pa, TransportConfiguration transport) throws CommandException {
		try {
			locator = HornetQClient.createServerLocatorWithoutHA(transport);
			factory = locator.createSessionFactory();
			session = factory.createSession(pa.getTarget().getUsername(), pa.getTarget().getPassword(), false, true, true, false, 0);
			session.start();
		}
		catch (Exception ex) {
			throw new CommandException("Could not connect to server", ex);
		}
	}


	protected void close() {
		try {
			if (getSession() != null) {
				getSession().close();
			}
			if (getFactory() != null) {
				getFactory().close();
			}
			if (getLocator() != null) {
				getLocator().close();
			}
		}
		catch (HornetQException hqex) {
			Output.error("Failed to close connection: " + hqex.getMessage());
		}
	}


	public ClientSession getSession() {
		return session;
	}


	public ClientSessionFactory getFactory() {
		return factory;
	}


	public ServerLocator getLocator() {
		return locator;
	}

}
