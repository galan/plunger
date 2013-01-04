package de.galan.plunger.command.hornetq;


import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * Abstraction for HornetQ Commands that make use of the HornetQ Core API
 * 
 * @author daniel
 */
public abstract class AbstractHornetqCoreCommand extends AbstractHornetqCommand {

	private ClientSession session;
	private ClientSessionFactory factory;
	private ServerLocator locator;


	@Override
	protected void initialize(PlungerArguments jca) throws CommandException {
		try {
			locator = HornetQClient.createServerLocatorWithoutHA(getTransportConfiguration(jca));
			factory = locator.createSessionFactory();
			session = factory.createSession(jca.getTarget().getUsername(), jca.getTarget().getPassword(), false, true, true, false, 0);
			session.start();
		}
		catch (Exception ex) {
			throw new CommandException("Could not connect to server", ex);
		}
	}


	@Override
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
