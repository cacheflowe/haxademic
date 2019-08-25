package com.haxademic.core.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Logger;

import com.haxademic.core.file.FileUtil;


public class WebServer {
	
	public static boolean DEBUG;
	public static int PORT = 8080;
	public static String WWW_PATH = "";
	protected WebServerRequestHandler handler;
	public static final String REQUEST_URL = "REQUEST_URL";


	public WebServer(WebServerRequestHandler handler, boolean debug) {
		this(handler, debug, FileUtil.getHaxademicWebPath());
	}
	
	public WebServer(WebServerRequestHandler handler, boolean debug, String wwwPath) {
		WebServer.DEBUG = debug;
		this.handler = handler;
		WWW_PATH = wwwPath;
		
		new Thread(new Runnable() { public void run() {
			initWebServer();
		}}).start();	
	}
	
	protected void initWebServer() {
		// silence jetty logging
		org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
		
		// init jetty server
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
	
	// Stop jetty logging (mostly). More info here: https://stackoverflow.com/a/19059551/352456 

	public class NoLogging implements Logger {
	    @Override public String getName() { return "no"; }
	    @Override public void warn(String msg, Object... args) { }
	    @Override public void warn(Throwable thrown) { }
	    @Override public void warn(String msg, Throwable thrown) { }
	    @Override public void info(String msg, Object... args) { }
	    @Override public void info(Throwable thrown) { }
	    @Override public void info(String msg, Throwable thrown) { }
	    @Override public boolean isDebugEnabled() { return false; }
	    @Override public void setDebugEnabled(boolean enabled) { }
	    @Override public void debug(String msg, Object... args) { }
	    @Override public void debug(Throwable thrown) { }
	    @Override public void debug(String msg, Throwable thrown) { }
	    @Override public Logger getLogger(String name) { return this; }
	    @Override public void ignore(Throwable ignored) { }
		@Override public void debug(String arg0, long arg1) { }
	}
}
