package com.haxademic.core.net;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import com.haxademic.core.app.P;

import processing.data.JSONObject;

//create a separate thread for the server not to freeze/interfere with Processing's default animation thread
public class SocketClient {
		
	public static boolean DEBUG = false;
	protected WebSocketClient client;
	protected ISocketClientDelegate delegate;
	public String serverAddress;// = "ws://xxx.xxx.xxx.xxx:8887";

	protected boolean SOCKET_DEBUG = false;
	protected int lastConnectAttemptTime = -1;
	protected int _userCaptureFoundTime = -1;
	protected int _curCountdownSecs = -1;
	protected int COUNTDOWN_TIME = 5;
	protected int SLOW_THROTTLE = 60;
	protected int FAST_THROTTLE = 5;
	
	public SocketClient(String serverAddress, ISocketClientDelegate delegate, boolean debug) {
		SocketClient.DEBUG = debug;
		this.serverAddress = (serverAddress != null) ? serverAddress : localSocketServerAddress();
		this.delegate = delegate;
		P.p.registerMethod("pre", this);
		
		new Thread(new Runnable() { public void run() {
			buildSocketClient();
		}}).start();	
	}
	
	public void setDelegate(ISocketClientDelegate delegate) {
		this.delegate = delegate;
	}
	
	protected void buildSocketClient() {		
		lastConnectAttemptTime = P.p.millis();
		
		try {
			client = new WebSocketClient( new URI( serverAddress ), new Draft_6455() ) {
				@Override
				public void onMessage( String message ) {
					if(SOCKET_DEBUG == true) P.println("onMessage: "+message);
					if(delegate != null) delegate.messageReceived(message);
				}

				@Override
				public void onOpen( ServerHandshake handshake ) {
					if(SOCKET_DEBUG == true) P.println( "opened connection" );
					if(delegate != null) delegate.socketConnected("self");
					JSONObject jsonOut = new JSONObject();
					jsonOut.setString("event", "SocketClient: opened connection");
					sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
				}

				@Override
				public void onClose( int code, String reason, boolean remote ) {
					if(delegate != null) delegate.socketDisconnected("self");
					if(SOCKET_DEBUG == true) P.println( "closed connection" );
				}

				@Override
				public void onError( Exception ex ) {
					if(SOCKET_DEBUG == true) P.println( "connection error" );
					ex.printStackTrace();
				}
			};
			client.connect();
			client.addHeader(serverAddress, serverAddress);
		} catch (URISyntaxException e) { e.printStackTrace(); }
	}
	
	public String localSocketServerAddress() {
		return "ws://" + IPAddress.getIP() + ":" + SocketServer.PORT;
	}

	public void pre() {
		checkConnection();
	}
	
	public boolean isConnected() {
//		_socketClient.getConnection().getReadyState();
//		return _socketClient.getReadyState() == WebSocket.READYSTATE; 
		if(client != null) return client.isOpen();
		return false;
	}
	
	public void sendMessage(String message) {
		if(isConnected() == false) return;
		client.send(message);
		if(SOCKET_DEBUG == true) P.println("sent message: "+message);
	}
	
	protected void checkConnection() {
		if(isConnected() == false) {
			if(P.p.millis() - lastConnectAttemptTime > 5000) {
				if(SOCKET_DEBUG == true) P.println("Attempting to reconnect to Websocket");
				new Thread(new Runnable() { public void run() {
					if(client != null) {
						try {
							client.reconnect();
						} catch (IllegalStateException e) {
							if(SOCKET_DEBUG == true) P.out("[SocketClient ERROR] client.reconnect() \n"+e.getMessage());
						}	
						lastConnectAttemptTime = P.p.millis();
					} else {
						buildSocketClient();
					}
				}}).start();	
			}
		}
	}
	
	public void disconnect() {
		P.p.unregisterMethod("pre", this);
		client.close();
	}
}