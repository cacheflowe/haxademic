package com.haxademic.core.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.jetty.server.Server;

public class WebServer {
	
	public static boolean DEBUG;
	public static int PORT = 8080;
	protected WebServerRequestHandler handler;

	public WebServer(WebServerRequestHandler handler, boolean debug) {
		WebServer.DEBUG = debug;
		this.handler = handler;
		
		new Thread(new Runnable() { public void run() {
			initWebServer();
		}}).start();	
	}
	
	protected void initWebServer() {
        Server server = new Server(WebServer.PORT);
        server.setHandler(this.handler);        

        // start the server!
        try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getServerAddress() {
		String serverBase = "";
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			// Get IP Address
			// byte[] ipAddr = addr.getAddress();
			// Get hostname
			// String hostname = addr.getHostName();
			serverBase = "http://" + addr.getHostAddress() + ":" + WebServer.PORT + "/";
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return serverBase;
	}

}
