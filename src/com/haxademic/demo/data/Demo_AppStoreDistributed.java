package com.haxademic.demo.data;

import com.cage.zxing4p3.ZXING4P;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.data.store.AppState;
import com.haxademic.core.data.store.AppStoreDistributed;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.IPAddress;
import com.haxademic.core.net.SocketServer;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.system.SystemUtil;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class Demo_AppStoreDistributed
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// event keys to send out
	protected String MOUSE_X = "MOUSE_X";
	protected String MOUSE_Y = "MOUSE_Y";
	
	// config
	protected boolean isServer = true;
	protected String socketServerAddress = null;// "ws://10.10.1.111:3001"; // null; // make null if we're running the server & client on the same machine
	
	// web server to serve up the html/js demo
	protected String wsServerAddress;
	protected WebServer webServer;
	protected String webServerAddress;
	protected PImage qrImage;
	
	protected StringBufferLog webServerLog = new StringBufferLog(10);

	protected String APP_STATE_ONE = "APP_STATE_ONE";
	protected String APP_STATE_TWO = "APP_STATE_TWO";

	protected void config() {
		Config.setAppSize(560, 680);
	}

	
	/////////////////////////////////
	// INIT
	/////////////////////////////////

	protected void firstFrame() {
		initAppStore();
		buildWebServer();
		
		// default AppStore values to prevent crash when we try to draw ellipse with shared position
		P.store.setNumber(MOUSE_X, 0);
		P.store.setNumber(MOUSE_Y, 0);
	}
	
	protected void initAppStore() {
		P.storeDistributed = AppStoreDistributed.instance();
		if(isServer == true) {
			P.storeDistributed.start(AppStoreDistributed.MODE_SERVER, null);
			wsServerAddress = "ws://" + IPAddress.getIP() + ":" + SocketServer.PORT;
		} else {
			if(socketServerAddress != null) {
				P.out("Starting socket client, connecting to server at ", socketServerAddress);
				P.storeDistributed.start(AppStoreDistributed.MODE_CLIENT, socketServerAddress);
				wsServerAddress = socketServerAddress;
			} else {
				P.storeDistributed.start(AppStoreDistributed.MODE_CLIENT, P.storeDistributed.localSocketServerAddress());
				wsServerAddress = P.storeDistributed.localSocketServerAddress();
			}
		}
		P.store.addListener(this);
		AppState.init(APP_STATE_ONE);

		// set to true to see messages coming in and out of the server
		// must be set after server init
		SocketServer.DEBUG = false;
	}
	
	protected void buildWebServer() {
		if(!isServer) return;
		
		webServer = new WebServer(new UIControlsHandler(), false);
		webServerAddress = WebServer.getServerAddress();
		
		ZXING4P qr = new ZXING4P();
		qrImage = qr.generateQRCode(webServerAddress + "app-store-distributed/", 128, 128);
	}
	
	/////////////////////////////////
	// INPUT
	/////////////////////////////////
	
	public void keyPressed() {
		super.keyPressed();
		if(isServer) {
			if(p.key == 'b') SystemUtil.openWebPage(WebServer.getServerAddress() + "app-store-distributed/");
		}
		if(p.key == '1') AppState.set(APP_STATE_ONE);
		if(p.key == '2') AppState.set(APP_STATE_TWO);
	}
	
	/////////////////////////////////
	// DRAW
	/////////////////////////////////
	
	protected void drawApp() {
		AppState.checkQueuedState();
		background(0);
		
		// set some shared values
		// and print P.store to debug
		sendSharedValues();
		P.store.showStoreValuesInDebugView();
		
		// draw debug values
		drawServerLocation();
		drawLogs();

		// draw mouse position
		// neat when shared from another instance of PAppletHax
		p.fill(255);
		p.noStroke();
		p.ellipse(P.store.getInt(MOUSE_X), P.store.getInt(MOUSE_Y), 20, 20);
	}
	
	protected void drawServerLocation() {
		if(wsServerAddress != null) {
			String fontFile = DemoAssets.fontOpenSansPath;
			PFont font = FontCacher.getFont(fontFile, 30);
			FontCacher.setFontOnContext(p.g, font, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
			p.text(wsServerAddress, 20, 10);
			p.rect(20, 60, pg.width - 40, 2);
			if(isServer) {
				p.text(webServerAddress, 20, 460);
				p.rect(20, 510, pg.width - 40, 2);
			}
			
			// draw client/server indication
			PFont fontSm = FontCacher.getFont(fontFile, 16);
			int textColor = (P.storeDistributed.isSocketConnected()) ? p.color(0, 255, 0) : p.color(255, 0, 0);
			FontCacher.setFontOnContext(p.g, fontSm, textColor, 1f, PTextAlign.RIGHT, PTextAlign.TOP);
			p.text((isServer) ? "Server" : "Client", 540, 24);
			
			// write out shared values
			fontSm = FontCacher.getFont(fontFile, 14);
			FontCacher.setFontOnContext(p.g, fontSm, p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
			int keyX = 20;
			int spacing = 16;
			int curY = 80;
			p.text("Numbers:", keyX, curY); curY += spacing;
			for (String key : P.store.numberKeys()) {
				p.text(key, keyX, curY);
				p.text(P.store.getNumber(key).floatValue(), 200, curY);
				curY += spacing;
			}
			curY += spacing;
			p.text("Strings:", keyX, curY); curY += spacing;
			for (String key : P.store.stringKeys()) {
				p.text(key, keyX, curY);
				p.text(P.store.getString(key), 200, curY);
				curY += spacing;
			}
			curY += spacing;
			p.text("Booleans:", keyX, curY); curY += spacing;
			for (String key : P.store.booleanKeys()) {
				p.text(key, keyX, curY);
				p.text(P.store.getBoolean(key)+"", 200, curY);
				curY += spacing;	
			}

			// show QR code to launch site
			if(isServer) {
				p.image(qrImage, pg.width - qrImage.width - 20, 530);
				DebugView.setTexture("qr", qrImage);
			}
		}
	}

	protected void drawLogs() {
		webServerLog.printToScreen(p.g, 20, 530);
	}
	
	/////////////////////////////////
	// SEND SHARED DATA
	/////////////////////////////////
	
	protected void sendSharedValues() {
		if(p.mouseX != p.pmouseX) P.storeDistributed.setNumber(MOUSE_X, p.mouseX);
		if(p.mouseY != p.pmouseY) P.storeDistributed.setNumber(MOUSE_Y, p.mouseY);
		if(FrameLoop.frameModLooped(100)) P.storeDistributed.setNumber("heartbeat", p.frameCount);
		if(FrameLoop.frameModLooped(200)) broadcastJson();	
	}
	
	protected void broadcastJson() {
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setBoolean("data", false);
	    jsonOut.setString("source", "java");
		P.storeDistributed.broadcastJson(jsonOut);
	}
	
	public void mouseClicked() {
		P.storeDistributed.setNumber("CLICK", p.frameCount);
	}
	
	/////////////////////////////////////////
	// AppStore callbacks
	/////////////////////////////////////////

	@Override
	public void updatedNumber(String key, Number val) {
//		DebugView.setValue(key, val.floatValue());
	}

	@Override
	public void updatedString(String key, String val) {
		if(key.equals(WebServer.REQUEST_URL)) {
			webServerLog.update(val);
		}
		if(key.equals(PEvents.KEY_PRESSED)) {
			P.storeDistributed.setString("KEY_SHARED", val);
		}
	}

	@Override
	public void updatedBoolean(String key, Boolean val) {
//		DebugView.setValue(key, val);
	}	

	public void updatedImage(String key, PImage val) {
		
	}
	
	public void updatedBuffer(String key, PGraphics val) {
		
	}


}

