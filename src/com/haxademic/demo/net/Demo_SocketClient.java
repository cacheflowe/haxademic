package com.haxademic.demo.net;

import java.util.UUID;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.ISocketClientDelegate;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.SocketClient;
import com.haxademic.core.net.SocketServer;

import processing.core.PFont;
import processing.data.JSONObject;

public class Demo_SocketClient
extends PAppletHax
implements ISocketClientDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected SocketClient socketClient;
	protected boolean isServerLocalhost = false;
	protected String serverAddress;
	protected StringBufferLog socketLog = new StringBufferLog(30);
	
	protected void config() {
		Config.setAppSize(560, 470);
		Config.setProperty(AppSettings.APP_NAME, "SocketClient");
	}
	
	protected void firstFrame() {
//		buildSocketClient();
		buildSocketClientPlusSix();
	}
	
	protected void buildSocketClient() {
		// works with Java SocketServer demos *or* ?room-id is used with Node ws-chatroom.js
//		SocketServer.PORT = 1337;
		serverAddress = (isServerLocalhost) ?
				"ws://" + IPAddress.getIP() + ":" + SocketServer.PORT + "?roomId=987654321" :
				"wss://192.168.1.154:3001";
		P.out(serverAddress);
		socketClient = new SocketClient(serverAddress, this, true);
	}
	
	protected void buildSocketClientPlusSix() {
		// works with PlusSix socket server w/authentication and auto-cycling QR codes & room IDs 
		String accId = "e448b1bb-a0db-4db9-90c8-55db9c7ec568";
		String accKey = "da71f60a-bacb-4666-aa26-4b7d36d4eed3";
		String roomId = UUID.randomUUID().toString();
		serverAddress = "ws://localhost:3001/ws?roomId="+roomId+"&clientType=kiosk&accountId="+accId+"&accountKey="+accKey;
		P.out(serverAddress);
		socketClient = new SocketClient(serverAddress, this, true);
	}
	
	protected void drawApp() {
		background(0);
		
		// draw debug
		pg.beginDraw();
		pg.background(0);
		drawServerLocation();
		pg.endDraw();
		p.image(pg, 0, 0);

		// send a simple message to clients
		if(p.mouseX != p.pmouseX || p.mouseY != p.pmouseY) sendTestMessage(); 
		
		// test shutting down & recreating the socket client
		if(KeyboardState.keyTriggered(' ')) {
			socketClient.disconnect();
			buildSocketClientPlusSix();
		}
	}
	
	protected void drawServerLocation() {
		String fontFile = DemoAssets.fontOpenSansPath;
		PFont font = FontCacher.getFont(fontFile, 30);
		FontCacher.setFontOnContext(pg, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		pg.text(serverAddress, 20, 10);
		pg.rect(20, 60, pg.width - 40, 2);
		
		PFont fontSm = FontCacher.getFont(fontFile, 16);
		if(socketClient.isConnected()) {
			FontCacher.setFontOnContext(pg, fontSm, p.color(0, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("Connected", 540, 24);
		} else {
			FontCacher.setFontOnContext(pg, fontSm, p.color(255, 0, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			pg.text("Disconnected", 540, 24);
		}
		
		socketLog.printToScreen(pg, 20, 80);
	}

	protected void sendTestMessage() {
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setString("store", "yes");
	    jsonOut.setString("type", "mouse");
	    jsonOut.setInt("x", p.mouseX);
	    jsonOut.setInt("y", p.mouseY);
	    String jsonString = JsonUtil.jsonToSingleLine(jsonOut);
		socketClient.sendMessage(jsonString);
		socketLog.update("OUT: " + jsonString);
	}
	
	////////////////////////////////////////////
	// ISocketClientDelegate methods
	////////////////////////////////////////////

	public void messageReceived(String message) {
		socketLog.update("IN:     " + message);
	}
	
	public void socketConnected(String connection) {
		socketLog.update("CONNECT: " + connection);
	}
	
	public void socketDisconnected(String connection) {
		socketLog.update("DISCONNECT: " + connection);
	}

}
