package com.haxademic.sketch.test;

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

import com.haxademic.core.app.PAppletHax;

public class WebSocketTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public void setup() {
		super.setup();	
		new ServerThread().start();
	}

	public void drawApp() {
		background(0);
	}

	//create a separate thread for the server not to freeze/interfere with Processing's default animation thread
	public class ServerThread extends Thread{
		@Override
		public void run(){
			try{
				WebSocketImpl.DEBUG = true;
				int port = 8887; // 843 flash policy port
				try {
					port = Integer.parseInt( args[ 0 ] );
				} catch ( Exception ex ) {
				}
				LocalServer s = new LocalServer( port );
				s.start();
				System.out.println( "WS Server started on port: " + s.getPort() );
				System.out.println( "WS Server started on ip: " + InetAddress.getLocalHost() );
				
				BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
				while ( true ) {
					String in = sysin.readLine();
					s.sendToAll( in );
				}
			}catch(IOException e){
				e.printStackTrace();
			}  
		}
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
			this.sendToAll( "new connection: " + handshake.getResourceDescriptor() );
			System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
		}

		@Override
		public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
			this.sendToAll( conn + " has left the room!" );
			System.out.println( conn + " has left the room!" );
		}

		@Override
		public void onMessage( WebSocket conn, String message ) {
			this.sendToAll( message );
			System.out.println( conn + ": " + message );
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