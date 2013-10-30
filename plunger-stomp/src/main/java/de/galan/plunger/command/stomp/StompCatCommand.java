package de.galan.plunger.command.stomp;

import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLException;

import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.client.ClientSubscription;
import org.projectodd.stilts.stomp.client.MessageHandler;
import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomp.client.SubscriptionBuilder;

import de.galan.plunger.command.CommandException;
import de.galan.plunger.command.generic.AbstractCatCommand;
import de.galan.plunger.domain.Message;
import de.galan.plunger.domain.PlungerArguments;
import de.galan.plunger.util.Output;


/**
 * daniel should have written a comment here.
 * 
 * @author daniel
 */
public class StompCatCommand extends AbstractCatCommand {

	/*
	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);

		HeartbeatContainer heartbeatContainer = new HeartbeatContainer();

		ClientNettyMessageGateway gateway = new ClientNettyMessageGateway();
		gateway.setPort(pa.getTarget().getPort());
		gateway.setHost(pa.getTarget().getHost());
		gateway.setHeartbeat(1000);

		ClientNettyChannelHandler channelHandler = new ClientNettyChannelHandler();
		channelHandler.setGateway(gateway);
		channelHandler.setHeartbeatContainer(heartbeatContainer);

		//gateway.addMessageListener(new IDontNeedSecurity()); // DON'T DO THIS!!!

		gateway.addMessageListener(new ClientMessageValidationListener());

		NettyConnectedMessageListener cml = new NettyConnectedMessageListener();
		cml.setHeartbeatContainer(heartbeatContainer);
		cml.setGateway(gateway);
		gateway.addMessageListener(cml);

		NettyDisconnectListenerAndInterceptor disconnect = new NettyDisconnectListenerAndInterceptor();
		disconnect.setCloseOnDisconnectMessage(false);
		gateway.addMessageListener(disconnect);
		gateway.addOutgoingMessageInterceptor(disconnect);
		disconnect.setGateway(gateway);

		gateway.setHandler(channelHandler);
	}
	*/

	private StompClient client;


	@Override
	protected void initialize(PlungerArguments pa) throws CommandException {
		super.initialize(pa);
		try {
			client = new StompClient("stomp://" + pa.getTarget().getHost() + ":" + pa.getTarget().getPort());
			client.connect();
		}
		catch (URISyntaxException ex) {
			throw new CommandException("bam1", ex);
		}
		catch (SSLException ex) {
			throw new CommandException("bam2", ex);
		}
		catch (InterruptedException ex) {
			throw new CommandException("bam3", ex);
		}
		catch (TimeoutException ex) {
			throw new CommandException("bam4", ex);
		}
		catch (StompException ex) {
			throw new CommandException("bam5", ex);
		}

		SubscriptionBuilder builder = client.subscribe("jms.queue.allwissend-events");
		//builder.withSelector(selector)
		//builder.withAckMode(AckMode.AUTO);
		builder.withMessageHandler(new MessageHandler() {

			@Override
			public void handle(StompMessage message) {
				Output.println("content: " + message.getContentAsString());
				// handle
			}
		});
		try {
			ClientSubscription subscription = builder.start();
			subscription.unsubscribe();
		}
		catch (StompException ex) {
			throw new CommandException("bam6", ex);
		}
		//client.send(StompMessages.createStompMessage(DESTINATION_QUEUE_ONE, "start"));
		//client.send(StompMessages.createStompMessage(DESTINATION_QUEUE_ONE, "stop"));
		//subscription.unsubscribe();
	}


	@Override
	protected Message getNextMessage(PlungerArguments pa) throws CommandException {
		return null;
	}


	@Override
	protected boolean isSystemHeader(String headerName) {
		return false;
	}


	@Override
	protected void close() {
		if (client != null) {
			try {
				client.disconnect();
			}
			catch (InterruptedException | TimeoutException | StompException ex) {
				//throw new CommandException("bam", ex);
			}
		}
	}

}
