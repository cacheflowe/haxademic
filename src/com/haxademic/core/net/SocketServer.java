package com.haxademic.core.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.java_websocket.WebSocketImpl;

import com.haxademic.core.app.P;

//create a separate thread for the server not to freeze/interfere with Processing's default animation thread
public class SocketServer {
	
	public static boolean DEBUG = false;
	public static int PORT = 8887;
	public static boolean FORWARDS_ALL_MESSAGES = true;

	SocketServerHandler server;
	
	public SocketServer(SocketServerHandler handler, boolean debug) {
		SocketServer.DEBUG = debug;
		this.server = handler;
		
		new Thread(new Runnable() { public void run() {
			initSocketServer();
		}}).start();	
	}
	
	protected void initSocketServer() {
		try{
			WebSocketImpl.DEBUG = false; // SocketServer.DEBUG;
			server.start();
			P.println( "WS Server started on port: " + server.getPort() );
			P.println( "WS Server started on ip: " + IPAddress.getLocalAddress() );
			
			BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
			while ( true ) {
				String in = sysin.readLine();
				server.sendToAll( in );
			}
		}catch(IOException e){
			e.printStackTrace();
		}  
	}
	
	public void sendMessage(String msg) {
		if(SocketServer.DEBUG == true) P.println("sending:");
		if(SocketServer.DEBUG == true) P.println(msg);
		server.sendToAll( msg );
	}
	
}