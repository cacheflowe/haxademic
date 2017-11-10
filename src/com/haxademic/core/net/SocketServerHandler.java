package com.haxademic.core.net;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.haxademic.core.app.P;

public class SocketServerHandler extends WebSocketServer {
	
	public SocketServerHandler( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
	}
	
	public SocketServerHandler( InetSocketAddress address ) {
		super( address );
	}
	
	protected String connAddress(WebSocket conn) {
		return conn.getRemoteSocketAddress().getHostName();
	}
	
	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		if(SocketServer.FORWARDS_ALL_MESSAGES) this.sendToAll( "{\"message\":\"new connection: " + handshake.getResourceDescriptor()+"\"}" );
		if(SocketServer.DEBUG == true) P.println( connAddress(conn) + " entered the room!" );
	}
	
	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		if(SocketServer.FORWARDS_ALL_MESSAGES) this.sendToAll( "{\"message\":\"" + connAddress(conn) + " has left the room!\"}" );
		if(SocketServer.DEBUG == true) P.println( connAddress(conn) + " has left the room!" );
	}
	
	@Override
	public void onMessage( WebSocket conn, String message ) {
		if(SocketServer.FORWARDS_ALL_MESSAGES) this.sendToAll( message );
		receiveMessage( message );
		if(SocketServer.DEBUG == true) P.println( connAddress(conn) + ": " + message );
	}
	
	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}
	
	protected void receiveMessage(String message) {
		// OVERRIDE THIS
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
		Collection<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}
}
