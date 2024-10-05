package com.haxademic.core.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
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
	
	public static boolean DEBUG = false;
	public static int PORT = 8080;
	public static int PORT_SSL = 443;
	public static boolean IS_SSL = false;
	public static final String REQUEST_URL = "REQUEST_URL";
	public static String WWW_PATH = "";	// deprecated from old, stupid static file server, but could be useful is an external class wants to know what the web path root is 
	protected AbstractHandler handler;
	protected HttpConfiguration httpConfiguration;
	protected Server server;
	protected String wwwPath;
	protected Boolean useSSL;
	protected Thread serverThread;

	public WebServer(AbstractHandler handler) {
		this(handler, false, FileUtil.haxademicWwwPath(), false);
	}
	
	public WebServer(AbstractHandler handler, boolean debug) {
		this(handler, debug, FileUtil.haxademicWwwPath(), false);
	}
	
	public WebServer(AbstractHandler handler, boolean debug, boolean addSSL) {
		this(handler, debug, FileUtil.haxademicWwwPath(), addSSL);
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
		if(server != null) return;
		disableLogging();
		createServer();
		configServer();
		// addNonSSL();
		if(useSSL) addSSL();
		startServer();
	}
	
	protected void disableLogging() {
		org.eclipse.jetty.util.log.Log.setLog(new NoLogging());
	}
	
	protected void createServer() {
		if(useSSL) {
			PORT = PORT_SSL;
			server = new Server();
		} else {
			server = new Server(PORT);
		}
		P.out("Starting WebServer at "+wwwPath+":"+PORT);
	}
	
	protected void configServer() {
		// init static web server
		WebAppContext webAppContext = new WebAppContext(wwwPath, "/");
		
		// turn off file locking! without this, we were blocked from dynamically replacing static files in the web server directory at runtime
		webAppContext.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
		
		// set custom & static handlers
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { 
			this.handler, 
			webAppContext 			// Jetty's built-in static asset web server. this catches any request not handled by the custom handler
		});
		server.setHandler(handlers);
	}
	
	protected void addNonSSL() {
		httpConfiguration = new HttpConfiguration();
		httpConfiguration.setSecureScheme("https");
		httpConfiguration.setSecurePort(PORT_SSL);

		final ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
		http.setPort(PORT);
		server.addConnector(http);
	}

	protected void addSSL() {
		// keystore creds
		String keyStorePath = P.path("haxademic/net/config/cert.p12");
		String keyStorePassword = "haxademic";
	
		// generate a self-signed cert:
		// ```
		// mkcert -cert-file cert.pem -key-file key.pem localhost
		// openssl pkcs12 -export -out cert.p12 -inkey key.pem -in cert.pem -name "localhost"
		// mkcert -cert-file cert.pem -key-file key.pem localhost
		// openssl pkcs12 -export -out cert.p12 -inkey key.pem -in cert.pem -name "localhost"
		// ```
		
		// Import into keystore - use password "changeit", which is the default password for a .crt downloaded from a server
		// $ "C:\Program Files\Eclipse Adoptium\jdk-17.0.4-Processing\bin\keytool.exe" -import -alias haxademic -keystore "C:\Program Files\Eclipse Adoptium\jdk-17.0.4-Processing\lib\security\cacerts" -file .\data\haxademic\net\config\server.crt

		try {
			SslContextFactory.Server ssl = new SslContextFactory.Server();
			ssl.setKeyStorePath(keyStorePath);
			ssl.setKeyStorePassword(keyStorePassword);
			ssl.setKeyManagerPassword(keyStorePassword);
			ssl.setTrustStorePath(keyStorePath);
			ssl.setTrustStorePassword(keyStorePassword);
			ssl.setKeyStoreType("PKCS12"); // "JKS" if not migrated
			// ssl.setKeyStoreType("JKS"); // "JKS" if not migrated
			ssl.setNeedClientAuth(false);
			ssl.setTrustAll(true);
			ssl.setValidateCerts(false);

			// build the connector
			ServerConnector https = new ServerConnector(server, ssl);
			https.setPort(PORT_SSL);
			// https.setIdleTimeout(30000L);
			// https.setHost("localhost");
			server.addConnector(https);

			System.out.println("SSL Connector added on port: " + PORT_SSL);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
					// initWebServer();
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
			String nonSSLPort = (IS_SSL) ? "" : ":" + WebServer.PORT;	// add port if not SSL. if is SSL, we don't need a port
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
