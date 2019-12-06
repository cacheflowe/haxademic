package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.ISocketClientDelegate;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.SocketClient;
import com.haxademic.core.net.SocketServer;

import processing.data.JSONObject;

public class Demo_SocketClient
extends PAppletHax
implements ISocketClientDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SocketClient wsClient;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.FPS, 90);
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	public void setupFirstFrame() {
	
		String serverAddress = "ws://" + IPAddress.getIP() + ":" + SocketServer.PORT;
		wsClient = new SocketClient(serverAddress, this, true);
	}
	
	protected void buildSocketServer() {
	}
	
	public void drawApp() {
		background(0);
		p.fill(255);
		// send a simple message to clients
		if(p.mouseX != p.pmouseX || p.mouseY != p.pmouseY) sendTestMessage(); 
	}
	
	protected void sendTestMessage() {
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setString("store", "yes");
	    jsonOut.setString("type", "mouse");
	    jsonOut.setInt("x", p.mouseX);
	    jsonOut.setInt("y", p.mouseY);
		wsClient.sendMessage(JsonUtil.jsonToSingleLine(jsonOut));
	}

	@Override
	public void messageReceived(String message) {
		P.out("Incoming WS message:");
	    DebugUtil.printBig(message);
	}

}
