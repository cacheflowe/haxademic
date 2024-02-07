package com.haxademic.core.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.haxademic.core.app.P;

public class SocketServer {
	
	public static boolean DEBUG = false;
	public static int PORT = 3001;
	public static boolean FORWARDS_ALL_MESSAGES = true;

	protected SocketServerHandler server;
	
	public SocketServer(SocketServerHandler handler, boolean debug) {
		SocketServer.DEBUG = debug;
		this.server = handler;
		
		new Thread(new Runnable() { public void run() {		// create a separate thread so the server doesn't freeze/interfere with Processing's animation thread
			initSocketServer();
		}}).start();	
	}
	
	protected void initSocketServer() {
		try {
			////////////////////////////////////////////////
			// Look into SSL addition: https://github.com/TooTallNate/Java-WebSocket/blob/master/src/main/example/SSLServerExample.java
			/*
			String STORETYPE = "JKS";
			String STOREPASSWORD = "haxademic";
			String KEYPASSWORD = "haxademic";

			KeyStore ks = KeyStore.getInstance( STORETYPE );
			File kf = new File( FileUtil.getPath("haxademic/net/config/haxademic-selfsigned.jks") );
			ks.load( new FileInputStream( kf ), STOREPASSWORD.toCharArray() );

			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( ks, KEYPASSWORD.toCharArray() );
			TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
			tmf.init( ks );

			SSLContext sslContext = null;
			sslContext = SSLContext.getInstance( "TLS" );
			sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );

			server.setWebSocketFactory( new DefaultSSLWebSocketServerFactory( sslContext ) );
			*/
			
			////////////////////////////////////////////////
			
			server.start();
			
			P.outInitLineBreak();
			P.outInit( "WS Server started on port: " + server.getPort() );
			P.outInit( "WS Server started on ip: " + IPAddress.getLocalAddress() );
			P.outInitLineBreak();
			
			BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
			while (true) { // sysin.ready()
				String in = sysin.readLine();
				P.outInitLineBreak();
				P.outInit("SocketServer :: relaying (to " + server.getConnections().size() + " clients):");
				P.outInit(in);
				P.outInitLineBreak();
				server.sendToAll( in );
			}
		} catch(IOException e) {	//  | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyStoreException | KeyManagementException
			e.printStackTrace();
		}  
	}

	public int numConnections() {
		return server.getConnections().size();
	}
	
	public void sendMessage(String msg) {
		if(SocketServer.DEBUG == true) {
			P.outInitLineBreak();
			P.outInit("SocketServer :: sending (to " + server.getConnections().size() + " clients):");
			P.outInit(msg);
			P.outInitLineBreak();
		}
		server.sendToAll( msg );
	}
	
}