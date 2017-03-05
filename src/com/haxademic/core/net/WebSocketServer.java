package com.haxademic.core.net;

import com.haxademic.core.app.P;
import com.haxademic.core.net.JSONUtil;
import com.haxademic.core.net.WebSocketRelay;

import processing.data.JSONObject;

public class WebSocketServer {

	protected SocketRelay _server;
	
	public WebSocketServer() {
		// fire up websocket server
		WebSocketRelay.DEBUG = false;
		try {
			_server = new SocketRelay();
			_server.start();
		} catch (Exception e) {
			P.println("Couldn't start Socket server!");
		}
	}
	
	public String hostName() {
		return "Server: ws://" + _server.localHost + ":" + _server.portStr;
	}
	
	///////////////////////
	// websocket helpers
	///////////////////////

	public void sendSocketMessage(String service, String command, String data) {
	    JSONObject jsonOut = new JSONObject();
		if(service != null) jsonOut.setString("service", service);
		if(command != null) jsonOut.setString("command", command);
		if(data != null)    jsonOut.setString("data", data);
	    _server.sendMessage(JSONUtil.jsonToSingleLine(jsonOut));
	}
	
	///////////////////////
	// Override of WebSocketRelay to act as a socket client with server listeners
	///////////////////////

	public class SocketRelay extends WebSocketRelay {
		
		protected void receiveMessage(String message) {
			if(message.indexOf("kinect_alive") != -1) {
				if(WebSocketRelay.DEBUG == true) P.println("receiveMessage", message);
				if(WebSocketRelay.DEBUG == true) P.println("lastKinectPingTime = ", P.p.millis());
//				_lastKinectPingTime = p.millis();
			}
		}
	}

}
