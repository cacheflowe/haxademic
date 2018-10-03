package com.haxademic.core.data.store;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.JSONUtil;
import com.haxademic.core.net.SocketServer;
import com.haxademic.core.net.SocketServerHandler;

import processing.data.JSONObject;

public class AppStoreDistributed {
	
	public static AppStoreDistributed instance;
	protected SocketServer server;
	protected WebSocketClient client;

	public AppStoreDistributed() {
		P.p.registerMethod("pre", this);
		P.out("AppStoreDistributed: CONVERT STRING CONSTANTS TO CONSTANTS");
	}
	
	public static AppStoreDistributed instance() {
		if(instance != null) return instance;
		instance = new AppStoreDistributed();
		return instance;
	}
	
	public void startServer() {
		// only one server or client should exist --------------------------------
		if(client == null && server == null) buildSocketServer();
	}
	
	public void startClient(String serverAddress) {
		if(client == null && server == null) buildSocketClient(serverAddress);
	}
	
	public String localSocketServerAddress() {
		return "ws://" + IPAddress.getIP() + ":" + SocketServer.PORT;
	}
	
	protected void buildSocketServer() {
		try {
			// SocketServer.PORT = 3000;
			server = new SocketServer(new AppStoreSocketHandler(SocketServer.PORT), true);
			P.p.debugView.setValue("WS Server", localSocketServerAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace(); 
		}
	}
	
	public void setNumber(String storeKey, Number val) {
		// pass along to AppStore
		P.store.setNumber(storeKey, val);
		// send to other machines
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setBoolean("store", true);
	    jsonOut.setString("type", "number");
	    jsonOut.setString("key", storeKey);
	    jsonOut.setFloat("value", val.floatValue());
		if(server != null) server.sendMessage(JSONUtil.jsonToSingleLine(jsonOut));
		if(client != null && client.isOpen()) client.send(JSONUtil.jsonToSingleLine(jsonOut));
	}
	
	public void setString(String storeKey, String val) {
		// pass along to AppStore
		P.store.setString(storeKey, val);
		// send to other machines
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setBoolean("store", true);
	    jsonOut.setString("type", "string");
	    jsonOut.setString("key", storeKey);
	    jsonOut.setString("value", val);
		if(server != null) server.sendMessage(JSONUtil.jsonToSingleLine(jsonOut));
		if(client != null && client.isOpen()) client.send(JSONUtil.jsonToSingleLine(jsonOut));
	}
	
	public void setBoolean(String storeKey, Boolean val) {
		// pass along to AppStore
		P.store.setBoolean(storeKey, val);
		// send to other machines
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setBoolean("store", true);
	    jsonOut.setString("type", "boolean");
	    jsonOut.setString("key", storeKey);
	    jsonOut.setBoolean("value", val);
		if(server != null) server.sendMessage(JSONUtil.jsonToSingleLine(jsonOut));
		if(client != null && client.isOpen()) client.send(JSONUtil.jsonToSingleLine(jsonOut));
	}
	
	
	/////////////////////////////////////////
	// WebSocket interface
	/////////////////////////////////////////
	
//	protected void receiveGenericMessage(String messageJSON) {
//		JSONObject eventData = JSONObject.parse(messageJSON);
//	    String event = eventData.getString("event");	
//	    String command = eventData.getString("command");	
//	    DebugUtil.printBig("Incoming WS message: " + event + " / " + command);
//	}
	
	/////////////////////////////////////////
	// Shared server/client code
	/////////////////////////////////////////

	protected void newMessage(String message) {
		// parse incoming json and pass along to correct AppStore data type
		P.println("CustomSocketHandler.receiveMessage() : ", message);
		JSONObject jsonData = JSONObject.parse(message);
		
		// if `store` key exists 
		if(!jsonData.isNull("store") && !jsonData.isNull("type")) {
			if(jsonData.getString("type").equals("string")) {
				P.store.setString(jsonData.getString("key"), jsonData.getString("value"));
			} else if(jsonData.getString("type").equals("number")) {
				P.store.setNumber(jsonData.getString("key"), jsonData.getFloat("value"));
			} else if(jsonData.getString("type").equals("boolean")) {
				P.store.setBoolean(jsonData.getString("key"), jsonData.getBoolean("value"));
			}
		} else {
			P.store.setString("NEW_JSON", message);
//			receiveGenericMessage(message);
		}

	}

	/////////////////////////////////////////
	// WebSocket server responder
	/////////////////////////////////////////

	public class AppStoreSocketHandler extends SocketServerHandler {

		protected PAppletHax delegate;
		
		public AppStoreSocketHandler(int port) throws UnknownHostException {
			super(port);
		}

		public void setDelegate(PAppletHax delegate) {
			this.delegate = delegate;
		}
		
		protected void receiveMessage(String message) {
			newMessage(message);
		}
	}

	/////////////////////////////////////////
	// WebSocket client
	/////////////////////////////////////////
	
	public String WEBSOCKET_SERVER_ADDRESS;// = "ws://process.local:8887";

	protected boolean SOCKET_DEBUG = false;
	protected int _lastConnectAttemptTime = -1;
	protected int _userCaptureFoundTime = -1;
	protected int _curCountdownSecs = -1;
	protected int COUNTDOWN_TIME = 5;
	protected int SLOW_THROTTLE = 60;
	protected int FAST_THROTTLE = 5;

	
	protected void buildSocketClient(String serverAddress) {
		if(serverAddress == null) WEBSOCKET_SERVER_ADDRESS = localSocketServerAddress();
		else WEBSOCKET_SERVER_ADDRESS = serverAddress;
		WEBSOCKET_SERVER_ADDRESS = "ws://" + IPAddress.getIP() + ":" + SocketServer.PORT;
		_lastConnectAttemptTime = P.p.millis();
		try {
			client = new WebSocketClient( new URI( WEBSOCKET_SERVER_ADDRESS ), new Draft_6455() ) {
				@Override
				public void onMessage( String message ) {
					if(SOCKET_DEBUG == true) P.println("onMessage: "+message);
					newMessage(message);
				}

				@Override
				public void onOpen( ServerHandshake handshake ) {
					if(SOCKET_DEBUG == true) P.println( "opened connection" );
					sendSocketMessage("process", "online", null);
				}

				@Override
				public void onClose( int code, String reason, boolean remote ) {
					if(SOCKET_DEBUG == true) P.println( "closed connection" );
				}

				@Override
				public void onError( Exception ex ) {
					if(SOCKET_DEBUG == true) P.println( "connection error" );
					ex.printStackTrace();
				}
			};
		} catch (URISyntaxException e) { e.printStackTrace(); }
		
		client.connect();
	}

	public void pre() {
		checkSocketConnection();
	}
	
	protected boolean isSocketConnected() {
//		_socketClient.getConnection().getReadyState();
//		return _socketClient.getReadyState() == WebSocket.READYSTATE; 
		if(server != null) return true;
		if(client != null) return client.isOpen();
		return false;
	}
	
	public void sendSocketMessage(String service, String command, String data) {
		if(isSocketConnected() == false) return;
		JSONObject jsonOut = new JSONObject();
		if(service != null) jsonOut.setString("service", service);
		if(command != null) jsonOut.setString("command", command);
		if(data != null)    jsonOut.setString("data", data);
		client.send(JSONUtil.jsonToSingleLine(jsonOut));
		if(SOCKET_DEBUG == true) P.println("sent JSON: "+JSONUtil.jsonToSingleLine(jsonOut));
	}
	
	protected void checkSocketConnection() {
		if(isSocketConnected() == false) {
			if(P.p.millis() - _lastConnectAttemptTime > 2000) {
				if(SOCKET_DEBUG == true) P.println("Attempting to reconnect to Websocket");
				buildSocketClient(null);
			}
		}
	}
	

	
}

