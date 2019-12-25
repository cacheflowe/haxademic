package com.haxademic.core.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;


public class WebServer {
	
	public static boolean DEBUG;
	public static int PORT = 8080;
	public static int PORT_SSL = 443;
	public static boolean IS_SSL = false;
	public static final String REQUEST_URL = "REQUEST_URL";
	public static String WWW_PATH = "";	// deprecated from old, stupid static file server, but could be useful is an external class wants to know what the web path root is 
	protected AbstractHandler handler;
	protected Server server;
	protected String wwwPath;
	protected Boolean useSSL;

	public WebServer(AbstractHandler handler) {
		this(handler, false, FileUtil.getHaxademicWebPath(), false);
	}
	
	public WebServer(AbstractHandler handler, boolean debug) {
		this(handler, debug, FileUtil.getHaxademicWebPath(), false);
	}
	
	public WebServer(AbstractHandler handler, boolean debug, boolean addSSL) {
		this(handler, debug, FileUtil.getHaxademicWebPath(), addSSL);
	}
	
	public WebServer(AbstractHandler handler, boolean debug, String wwwPath, boolean useSSL) {
		WebServer.DEBUG = debug;
		this.handler = handler;
		this.wwwPath = wwwPath;
		this.useSSL = useSSL;
		WWW_PATH = wwwPath;
		IS_SSL = useSSL;
		
		new Thread(new Runnable() { public void run() {
			initWebServer();
		}}).start();
	}
	
	protected void initWebServer() {
		disableLogging();
		createServer();
		configServer();
		if(useSSL) addSSL();
	    startServer();
	}
	
	protected void disableLogging() {
		org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
	}
	
	protected void createServer() {
//		if(useSSL) PORT = PORT_SSL;
		P.out("Starting WebServer at "+wwwPath+":"+PORT);
        server = new Server(PORT);
	}
	
	protected void configServer() {
        // init static web server
        WebAppContext webAppContext = new WebAppContext(wwwPath, "/");
        
        // turn off file locking! 
        // without this, we were blocked from dynamically replace static files in the web server directory at runtime
        webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        
        // set custom & static handlers
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { 
    		this.handler, 
    		webAppContext 			// Jetty's built-in static asset web server. this catches any request not handled by the custom handler
        });
        server.setHandler(handlers);
	}
	
	protected void addSSL() {
        // add self-signed ssl
		// generate a self-signed cert: 
		// $ keytool -genkey -keyalg RSA -alias tomcat -keystore selfsigned.jks -validity 9999 -keysize 2048
		// password: haxademic
		// Migrate to pkcs12
		// $ keytool -importkeystore -srckeystore selfsigned.jks -destkeystore selfsigned.jks -deststoretype pkcs12
		SslContextFactory.Server ssl = new SslContextFactory.Server();
	    ssl.setKeyStorePath(FileUtil.getPath("haxademic/net/config/haxademic-selfsigned.jks"));
	    ssl.setKeyStorePassword("haxademic");
	    ssl.setKeyManagerPassword("haxademic");
	    ssl.setKeyStoreType("PKCS12"); // "JKS" if not migrated
	    ssl.setNeedClientAuth(false);
	    ssl.setTrustAll(true);
	    ssl.setValidateCerts(false);
	    // build the connector
	    final ServerConnector https = new ServerConnector(server, ssl);
	    https.setPort(PORT_SSL);
//	    https.setIdleTimeout(30000L);
//	    https.setHost("localhost");
	    server.addConnector(https);
	}
	
	protected void startServer() {
        try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// public
	
	public Server server() {
		return server;
	}
	
	public void stop() {
		try {
			if(server != null) server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void restart() {
		new Thread(new Runnable() { public void run() {
			try {
				if(server != null) {
					server.stop();
					Thread.sleep(1000);		// let ports clear up: https://stackoverflow.com/a/15240883/352456
					initWebServer();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}}).start();	
	}
	
	public static String getServerAddress() {
		String serverBase = "";
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			// byte[] ipAddr = addr.getAddress();
			// String hostname = addr.getHostName();
			String protocol = (IS_SSL) ? "https://" : "http://";
			String nonSSLPort = (IS_SSL) ? "" : ":" + WebServer.PORT;	// add port if not SSL. if is SSL< we don't need any port
			serverBase = protocol + addr.getHostAddress() + nonSSLPort + "/";
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
