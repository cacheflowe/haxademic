package com.haxademic.core.data.store;

import java.net.UnknownHostException;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.ISocketClientDelegate;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.SocketClient;
import com.haxademic.core.net.SocketServer;
import com.haxademic.core.net.SocketServerHandler;

import processing.data.JSONObject;

public class AppStoreDistributed
implements ISocketClientDelegate {
	
	public static AppStoreDistributed instance;
	public static int MODE_SERVER = 0;
	public static int MODE_CLIENT = 1;
	public static boolean setLocalAuto = true; // if the ws:// server is external (ex: Node), we don't want to store immediately, but wait for the bounce-back, like the .js version 
	protected SocketServer server;
	protected SocketClient client;
	
	public static final String SOCKET_CONNECTED = "SOCKET_CONNECTED";
	public static final String SOCKET_DISCONNECTED = "SOCKET_DISCONNECTED";
	public static final String DATA_TYPE = "type";
	public static final String DATA_TYPE_NUMBER = "number";
	public static final String DATA_TYPE_STRING = "string";
	public static final String DATA_TYPE_BOOLEAN = "boolean";
	public static final String DATA_TYPE_JSON_KEY = "json";
	public static final String STORE_KEY = "store";
	public static final String JSON_KEY = "key";
	public static final String JSON_VALUE = "value";

	public AppStoreDistributed() {}
	
	public static AppStoreDistributed instance() {
		if(instance != null) return instance;
		instance = new AppStoreDistributed();
		return instance;
	}
	
	public void start(int mode, String serverAddress) {
		if(mode == MODE_SERVER) {
			startServer();
		} else {
			startClient(serverAddress);
		}
	}
	
	public void start(SocketClient client) {
		this.client = client;
		this.client.setDelegate(this);
	}
	
	protected void startServer() {
		// only one server or client should exist --------------------------------
		if(client == null && server == null) buildSocketServer();
	}
	
	protected void startClient(String serverAddress) {
		if(client == null && server == null) buildSocketClient(serverAddress);
	}
	
	public String localSocketServerAddress() {
		return "ws://" + IPAddress.getIP() + ":" + SocketServer.PORT;
	}
	
	protected void buildSocketServer() {
		try {
			// SocketServer.PORT = 3000;
			server = new SocketServer(new SocketServerHandler(SocketServer.PORT, this), false);
			DebugView.setValue("WS Server", localSocketServerAddress());
		} catch (UnknownHostException e) {
			// e.printStackTrace(); 
		}
	}
	
	protected void buildSocketClient(String serverAddress) {
		if(serverAddress == null) serverAddress = localSocketServerAddress(); // use local machine if remote address isn't specified
		client = new SocketClient(serverAddress, this, true);
	}
	
	public boolean isSocketConnected() {
		if(server != null) return true;
		if(client != null) return client.isConnected();
		return false;
	}
	
	public int numConnections() {
		if(server != null) return server.numConnections();
		if(client != null) return client.isConnected() ? 1 : 0;
		return 0;
	}
	
	// Shared basic data types setters 
	
	public void setNumber(String storeKey, Number val) {
		// pass along to AppStore
		if(setLocalAuto) P.store.setNumber(storeKey, val);
		// send to other machines
		JSONObject jsonOut = new JSONObject();
		jsonOut.setBoolean(STORE_KEY, true);
		jsonOut.setString(DATA_TYPE, DATA_TYPE_NUMBER);
		jsonOut.setString(JSON_KEY, storeKey);
		jsonOut.setFloat(JSON_VALUE, val.floatValue());
		if(server != null) server.sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
		if(client != null && client.isConnected()) client.sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
	}
	
	public void setString(String storeKey, String val) {
		// pass along to AppStore
		if(setLocalAuto) P.store.setString(storeKey, val);
		// send to other machines
		JSONObject jsonOut = new JSONObject();
		jsonOut.setBoolean(STORE_KEY, true);
		jsonOut.setString(DATA_TYPE, DATA_TYPE_STRING);
		jsonOut.setString(JSON_KEY, storeKey);
		jsonOut.setString(JSON_VALUE, val);
		if(server != null) server.sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
		if(client != null && client.isConnected()) client.sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
	}
	
	public void setBoolean(String storeKey, Boolean val) {
		// pass along to AppStore
		if(setLocalAuto) P.store.setBoolean(storeKey, val);
		// send to other machines
		JSONObject jsonOut = new JSONObject();
		jsonOut.setBoolean(STORE_KEY, true);
		jsonOut.setString(DATA_TYPE, DATA_TYPE_BOOLEAN);
		jsonOut.setString(JSON_KEY, storeKey);
		jsonOut.setBoolean(JSON_VALUE, val);
		if(server != null) server.sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
		if(client != null && client.isConnected()) client.sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
	}
	
	public void broadcastJson(JSONObject jsonOut) {
		if(server != null) server.sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
		if(client != null && client.isConnected()) client.sendMessage(JsonUtil.jsonToSingleLine(jsonOut));		
	}
	
	/////////////////////////////////////////
	// ISocketClientDelegate: Shared server/client callback & status
	/////////////////////////////////////////

	public void messageReceived(String message) {
		// parse incoming json and pass along to correct AppStore data type
		// P.println("CustomSocketHandler.receiveMessage() : ", message);
		JSONObject jsonData = JSONObject.parse(message);
		
		// if `store` key exists, set on local store
		if(!jsonData.isNull(STORE_KEY) && !jsonData.isNull(DATA_TYPE)) {
			String key = jsonData.getString(JSON_KEY);
			if(jsonData.getString(DATA_TYPE).equals(DATA_TYPE_STRING)) {
				String val = jsonData.getString(JSON_VALUE);
				if(key.equals(AppState.APP_STATE)) { // special case for APP_STATE - we queue it so it behaves as expected
					AppState.set(val);
				} else {
					P.store.setString(key, val);
				}
			} else if(jsonData.getString(DATA_TYPE).equals(DATA_TYPE_NUMBER)) {
				P.store.setNumber(key, jsonData.getFloat(JSON_VALUE));
			} else if(jsonData.getString(DATA_TYPE).equals(DATA_TYPE_BOOLEAN)) {
				P.store.setBoolean(key, jsonData.getBoolean(JSON_VALUE));
			}
		} else {
			P.store.setString(DATA_TYPE_JSON_KEY, message);
		}
	}
	
	public void socketConnected(String connection) {
		P.store.setString(SOCKET_CONNECTED, connection);
	}
	
	public void socketDisconnected(String connection) {
		P.store.setString(SOCKET_DISCONNECTED, connection);
	}

}

