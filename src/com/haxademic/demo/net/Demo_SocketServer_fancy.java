
package com.haxademic.demo.net;

import java.net.UnknownHostException;

import com.cage.zxing4p3.ZXING4P;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.ISocketClientDelegate;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.net.SocketServer;
import com.haxademic.core.net.SocketServerHandler;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.system.SystemUtil;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class Demo_SocketServer_fancy
extends PAppletHax
implements IAppStoreListener, ISocketClientDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/////////////////////////////////
	// PROPERTIES
	/////////////////////////////////
	
	protected boolean DEBUG = true;
	protected SocketServerHandler socketServerHandler;
	protected SocketServer socketServer;
	protected String wsServerAddress;
	protected WebServer webServer;
	protected String webServerAddress;
	protected PImage qrImage;
	
	protected StringBufferLog socketLog = new StringBufferLog(30);
	protected StringBufferLog webServerLog = new StringBufferLog(10);

	/////////////////////////////////
	// INIT
	/////////////////////////////////
	
	protected void config() {
		Config.setAppSize(560, 680);
	}
	
	protected void firstFrame() {
		// init state
		P.store.addListener(this);
		
		// build screens / objects
		buildSocketServer();
		buildWebServer();
		
		// extra setup
		addKeyCommandInfo();
	}	
	
	protected void addKeyCommandInfo() {
		DebugView.setHelpLine(DebugView.TITLE_PREFIX + "Custom Key Commands", "");
		DebugView.setHelpLine("[b] |", "Launch Socket Test URL");
	}
	
	protected void buildWebServer() {
		WebServer.PORT = 8080;
		webServer = new WebServer(new UIControlsHandler(), false);
		webServerAddress = WebServer.getServerAddress();
		
		ZXING4P qr = new ZXING4P();
		qrImage = qr.generateQRCode(webServerAddress + "web-socket-demo/", 128, 128);
	}
	
	protected void buildSocketServer() {
		try {
			SocketServer.PORT = 3001;
			socketServerHandler = new SocketServerHandler(SocketServer.PORT, this);
			socketServer = new SocketServer(socketServerHandler, DEBUG);
			wsServerAddress = "ws://" + IPAddress.getIP() + ":" + SocketServer.PORT;
			DebugView.setValue("WS Server", wsServerAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace(); 
		}
	}
	
	/////////////////////////////////
	// INPUT
	/////////////////////////////////
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'b') SystemUtil.openWebPage(WebServer.getServerAddress() + "web-socket-demo/#machine-1");
	}
	
	/////////////////////////////////
	// DRAW
	/////////////////////////////////
	
	protected void drawApp() {
		// main app canvas context setup
		p.background(0);
		p.noStroke();
		PG.setDrawCorner(p);

		// MAIN DRAW STEPS:
		pg.beginDraw();
		pg.background(0);
		drawServerLocation();
		drawLogs();
		pg.endDraw();
		p.image(pg, 0, 0);
		
		// debug
		P.store.showStoreValuesInDebugView();
		if(p.frameCount % 100 == 0) sendHeartbeat();
	}
	
	protected void drawServerLocation() {
		if(wsServerAddress != null && webServerAddress != null) {
			String fontFile = DemoAssets.fontOpenSansPath;
			PFont font = FontCacher.getFont(fontFile, 30);
			FontCacher.setFontOnContext(pg, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
			pg.text(wsServerAddress, 20, 10);
			pg.rect(20, 60, pg.width - 40, 2);
			pg.text(webServerAddress, 20, 460);
			pg.rect(20, 510, pg.width - 40, 2);
			
			PFont fontSm = FontCacher.getFont(fontFile, 16);
			FontCacher.setFontOnContext(pg, fontSm, p.color(0, 255, 0), 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			int numConns = socketServerHandler.getConnections().size();
			pg.text(numConns + " connections", 540, 24);
			
			pg.image(qrImage, pg.width - qrImage.width - 20, 530);
			DebugView.setTexture("qr", qrImage);
		}
	}

	protected void drawLogs() {
		socketLog.printToScreen(pg, 20, 80);
		webServerLog.printToScreen(pg, 20, 530);
	}
	
	/////////////////////////////////
	// SOCKET MESSAGING
	/////////////////////////////////	
	
	protected void sendHeartbeat() {
		JSONObject jsonOut = new JSONObject();
		jsonOut.setString("event", "heartbeat");
		jsonOut.setInt("value", p.frameCount);
		String jsonString = JsonUtil.jsonToSingleLine(jsonOut);
		socketServer.sendMessage(jsonString);
		socketLog.printToScreen(pg, 20, 80);
	}
	
	/////////////////////////////////
	// APPSTORE LISTENERS
	/////////////////////////////////

	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {
		if(key.equals(WebServer.REQUEST_URL)) {
			P.out("WebServer.REQUEST_URL", val);
			webServerLog.update(val);
		}
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
		
	/////////////////////////////////
	// ISocketClientDelegate methods
	/////////////////////////////////
	
	public void messageReceived(String message) {
		socketLog.update("IN:     " + message);
		
		// this check is specifically listening for events from `www/web-socket-demo/`
		/*
		if(message.indexOf("WEB_EVENT") != -1) {
			JSONObject eventData = JSONObject.parse(message);
				String event = eventData.getString("event");	
				String command = eventData.getString("command");	
				DebugUtil.printBig("Incoming WS command: " + event + " / " + command);
		}
		*/
	}

	public void socketConnected(String connection) {
		socketLog.update("CONNECT: " + connection);
	}
	
	public void socketDisconnected(String connection) {
		socketLog.update("DISCONNECT: " + connection);
	}

}
