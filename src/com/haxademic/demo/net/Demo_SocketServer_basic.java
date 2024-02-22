package com.haxademic.demo.net;

import java.net.UnknownHostException;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.ISocketClientDelegate;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.SocketServer;
import com.haxademic.core.net.SocketServerHandler;
import com.haxademic.core.system.SystemUtil;

import processing.data.JSONObject;

public class Demo_SocketServer_basic
extends PAppletHax
implements ISocketClientDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SocketServer wsServer;
	
	protected void config() {
		Config.setProperty(AppSettings.FPS, 90);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		buildSocketServer();
		SystemUtil.openWebPage("http://localhost/haxademic/www/web-socket-demo/");
	}
	
	protected void buildSocketServer() {
		try {
			// SocketServer.PORT = 3000;
			wsServer = new SocketServer(new SocketServerHandler(SocketServer.PORT, this), true);
			DebugView.setValue("WS Server", "ws://" + IPAddress.getIP() + ":" + SocketServer.PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace(); 
		}
	}
	
	protected void drawApp() {
		background(0);
		p.fill(255);
		// send a simple message to clients
		if(p.frameCount % 100 == 0) sendFrameMessage(); 
	}
	
	protected void sendFrameMessage() {
		JSONObject jsonOut = new JSONObject();
		jsonOut.setString("event", "frame-count");
		jsonOut.setInt("frame", p.frameCount);
		wsServer.sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
	}
	
	/////////////////////////////////////
	// ISocketClientDelegate delegate methods
	/////////////////////////////////////
	
	@Override
	public void messageReceived(String message) {
		// this check is specifically listening for events from `www/web-socket-demo/`
		if(message.indexOf("web-event") != -1) {
			JSONObject eventData = JSONObject.parse(message);
			String event = eventData.getString("event");	
			String command = eventData.getString("command");	
			DebugUtil.printBig("Incoming WS message: " + event + " / " + command);
		}
	}

	public void socketConnected(String connection) {
		P.out("socketConnected:", connection);
	}
	
	public void socketDisconnected(String connection) {
		P.out("socketDisconnected:", connection);
	}


}
