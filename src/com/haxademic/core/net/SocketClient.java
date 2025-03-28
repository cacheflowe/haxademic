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
		
	protected WebSocketClient client;
	protected ISocketClientDelegate delegate;
	public String serverAddress;// = "ws://xxx.xxx.xxx.xxx:8887";

	public static boolean DEBUG = false;
	protected int lastConnectAttemptTime = -1;
	protected int _userCaptureFoundTime = -1;
	protected int _curCountdownSecs = -1;
	public static int RECONNECT_TIME = 1000 * 60;
	
	public SocketClient(String serverAddress, ISocketClientDelegate delegate, boolean debug) {
		DEBUG = debug;
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
					if(DEBUG == true) P.out("onMessage: "+message);
					if(delegate != null) delegate.messageReceived(message);
				}

				@Override
				public void onOpen( ServerHandshake handshake ) {
					if(DEBUG == true) P.out( "opened connection" );
					if(delegate != null) delegate.socketConnected("self");
					JSONObject jsonOut = new JSONObject();
					jsonOut.setString("event", "SocketClient: opened connection");
					sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
				}

				@Override
				public void onClose( int code, String reason, boolean remote ) {
					if(delegate != null) delegate.socketDisconnected("self");
					if(DEBUG == true) P.out( "closed connection:", code, reason, remote );
				}

				@Override
				public void onError( Exception ex ) {
					if(DEBUG == true) P.out( "connection error", ex.getMessage() );
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
		if(DEBUG == true) P.out("sent message: "+message);
	}
	
	protected void checkConnection() {
		if(isConnected() == false) {
			if(P.p.millis() - lastConnectAttemptTime > RECONNECT_TIME) {
				if(DEBUG == true) P.out("Attempting to reconnect to Websocket");
				new Thread(new Runnable() { public void run() {
					if(client != null) {
						try {
							client.reconnect();
						} catch (IllegalStateException e) {
							if(DEBUG == true) P.out("[SocketClient ERROR] client.reconnect() \n"+e.getMessage());
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
		if(isConnected()) client.close();
	}
}