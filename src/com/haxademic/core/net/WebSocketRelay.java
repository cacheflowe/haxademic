package com.haxademic.core.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.haxademic.core.app.P;

//create a separate thread for the server not to freeze/interfere with Processing's default animation thread
public class WebSocketRelay extends Thread {
	LocalServer localServer;
	public String portStr;
	public String localHost;
	
	@Override
	public void run(){
		try{
			WebSocketImpl.DEBUG = true;
			int port = 8887; // 843 flash policy port
			try {
				port = Integer.parseInt( P.p.args[ 0 ] );
			} catch ( Exception ex ) {
			}
			localServer = new LocalServer( port );
			localServer.start();
			P.println( "WS Server started on port: " + localServer.getPort() );
			P.println( "WS Server started on ip: " + InetAddress.getLocalHost() );
			portStr = ""+localServer.getPort();
			localHost = ""+InetAddress.getLocalHost();
			
			BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
			while ( true ) {
				String in = sysin.readLine();
				localServer.sendToAll( in );
			}
		}catch(IOException e){
			e.printStackTrace();
		}  
	}
	
	public void sendMessage(String msg) {
		P.println("sending:");
		P.println(msg);
		localServer.sendToAll( msg );
	}
	
	public class LocalServer extends WebSocketServer {
		
		public LocalServer( int port ) throws UnknownHostException {
			super( new InetSocketAddress( port ) );
		}
		
		public LocalServer( InetSocketAddress address ) {
			super( address );
		}
		
		@Override
		public void onOpen( WebSocket conn, ClientHandshake handshake ) {
			this.sendToAll( "{\"message\":\"new connection: " + handshake.getResourceDescriptor()+"\"}" );
			P.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
		}
		
		@Override
		public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
			this.sendToAll( "{\"message\":\"" + conn + " has left the room!\"}" );
			P.println( conn + " has left the room!" );
		}
		
		@Override
		public void onMessage( WebSocket conn, String message ) {
			this.sendToAll( message );
			P.println( conn + ": " + message );
		}
		
		@Override
		public void onError( WebSocket conn, Exception ex ) {
			ex.printStackTrace();
			if( conn != null ) {
				// some errors like port binding failed may not be assignable to a specific websocket
			}
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
}