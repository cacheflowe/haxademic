package com.haxademic.demo.net;

import java.net.UnknownHostException;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.SocketServer;
import com.haxademic.core.net.SocketServerHandler;
import com.haxademic.core.system.SystemUtil;

import processing.data.JSONObject;

public class Demo_SocketServer
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SocketServer server;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.FPS, 90);
	}

	public void setup() {
		super.setup();	
		buildSocketServer();
		SystemUtil.openWebPage("http://localhost/_open-source/haxademic/www/web-socket-demo/");
	}
	
	protected void buildSocketServer() {
		try {
			// SocketServer.PORT = 3000;
			server = new SocketServer(new CustomSocketHandler(SocketServer.PORT), true);
			p.debugView.setValue("WS Server", "ws://" + IPAddress.getIP() + ":" + SocketServer.PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace(); 
		}
	}
	
	public void drawApp() {
		background(0);
		p.fill(255);
		// send a simple message to clients
		if(p.frameCount % 100 == 0) sendFrameMessage(); 
	}
	
	protected void sendFrameMessage() {
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setString("event", "frame-count");
	    jsonOut.setInt("frame", p.frameCount);
		server.sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
	}
	
	protected void receiveWebMessage(String messageJSON) {
		JSONObject eventData = JSONObject.parse(messageJSON);
	    String event = eventData.getString("event");	
	    String command = eventData.getString("command");	
	    DebugUtil.printBig("Incoming WS message: " + event + " / " + command);
	}
	
	// Example custom WebSocket server responder

	public class CustomSocketHandler extends SocketServerHandler {

		protected PAppletHax delegate;
		
		public CustomSocketHandler(int port) throws UnknownHostException {
			super(port);
		}

		public void setDelegate(PAppletHax delegate) {
			this.delegate = delegate;
		}
		
		protected void receiveMessage(String message) {
			P.println("CustomSocketHandler.receiveMessage() : ", message);
			if(message.indexOf("web-event") != -1) receiveWebMessage(message);
		}
	}

}
