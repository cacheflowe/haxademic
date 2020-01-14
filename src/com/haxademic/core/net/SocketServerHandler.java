package com.haxademic.core.net;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.haxademic.core.app.P;

public class SocketServerHandler 
extends WebSocketServer {
	
	protected ISocketClientDelegate delegate;
	
	public SocketServerHandler(int port, ISocketClientDelegate delegate) throws UnknownHostException {
		super(new InetSocketAddress( port ));
		this.delegate = delegate;
	}
	
//	public SocketServerHandler(InetSocketAddress address) {
//		super( address );
//	}
	
	protected static final String connAddressError = "Error: no connAddress"; 
	protected String connAddress(WebSocket conn) {
		if(conn == null) return connAddressError;
		if(conn.getRemoteSocketAddress() == null) return connAddressError;
		if(conn.isClosed()) return connAddressError;
		if(conn.getRemoteSocketAddress().getHostName() == null) return connAddressError;
		return conn.getRemoteSocketAddress().getHostName();
	}
	
	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		// handshake.getResourceDescriptor()
		if(conn != null) {
			if(delegate != null) delegate.socketConnected(connAddress(conn));
			if(SocketServer.FORWARDS_ALL_MESSAGES) this.sendToAll( "{\"message\":\"new connection: " + connAddress(conn) + " has entered the room.\"}" );
			if(SocketServer.DEBUG == true) {
				P.out( connAddress(conn) + " entered the room!" );
				P.out("Connections: "+getConnections().size());
			}
		}
	}
	
	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		if(delegate != null) delegate.socketDisconnected(connAddress(conn));
		if(conn != null) {
			if(SocketServer.FORWARDS_ALL_MESSAGES) this.sendToAll( "{\"message\":\"" + connAddress(conn) + " has left the room.\"}" );
			if(SocketServer.DEBUG == true) P.out( connAddress(conn) + " has left the room!" );
		}
	}
	
	@Override
	public void onMessage( WebSocket conn, String message ) {
		if(conn != null) {
			if(SocketServer.FORWARDS_ALL_MESSAGES) this.sendToAll( message );
			receiveMessage( message );
			if(SocketServer.DEBUG == true) P.out( connAddress(conn) + ": " + message );
		}
	}
	
	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}
	
	protected void receiveMessage(String message) {
		// OVERRIDE THIS if subclassing
		if(delegate != null) delegate.messageReceived(message);
	}
	
	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * 
	 * @param text
	 *            The String to send across the network.
	 * @throws InterruptedException
	 *             When socket related I/O errors occur.
	 */
	public void sendToAll( String text ) {
		Collection<WebSocket> con = getConnections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				if(c != null && c.isOpen() == true && c.isClosed() == false && c.isClosing() == false) {
					c.send( text );
				}
			}
		}
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}
}
