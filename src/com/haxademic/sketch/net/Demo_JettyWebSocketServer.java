package com.haxademic.sketch.net;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

public class Demo_JettyWebSocketServer
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	Server server;
	
	protected void firstFrame() {
		server = new Server();
		
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(8787);
		server.addConnector(connector);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/websocket");
		server.setHandler(context);

		try {
			ServerContainer wscontainer = WebSocketServerContainerInitializer.initialize(context);
			wscontainer.addEndpoint(WebsocketEndpoint.class);
			synchronized (server) {
				server.start();
			}
			
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}
	
	protected void drawApp() {
		p.background(0);
	}

	/**
	 * Internal class to handle websocket messages
	 */
//	@ClientEndpoint
//	@ServerEndpoint("/websocket")
	public static class WebsocketEndpoint {

		/**
		 * Synchronized list of connected clients to websocket server
		 */
		private static Set<Session> clients = Collections
				.synchronizedSet(new HashSet<Session>());

		/**
		 * This method is invoked when a new message is received
		 * 
		 * @param message Received string message
		 * @param session  Client reference
		 * @throws IOException
		 */
		@OnMessage
		public void onMessage(String message, Session session)
				throws IOException {
			P.out("New message: " + message + " from client: " + session);
			synchronized (clients) {
				for (Session client : clients) {
					if (!client.equals(session)) {
						client.getBasicRemote().sendText(message);
					}
				}
			}
		}

		/**
		 * This method is invoked when a new client is connected
		 * @param session New client session
		 */
		@OnOpen
		public void onOpen(Session session) {
			P.out("New connection to websocket endpoint: " + session);
			clients.add(session);
		}

		/**
		 * This method is invoked when a client is disconnected or an error
		 * occurs.
		 * 
		 * @param session session of the affected user
		 */
		@OnClose
		public void onClose(Session session, CloseReason reason) {
			P.out("Disconnected client, reason "+ reason +" session:" + session);
			clients.remove(session);
		}
		
		@OnError
	    public void onWebSocketError(Throwable cause){
	        cause.printStackTrace(System.err);
	    }
	}
	
}

