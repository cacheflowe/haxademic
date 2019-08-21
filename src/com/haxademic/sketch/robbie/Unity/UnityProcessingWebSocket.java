package com.haxademic.sketch.robbie.Unity;

import com.haxademic.core.app.P;
import com.haxademic.core.data.store.AppStoreDistributed;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.net.ISocketClientDelegate;
import com.haxademic.core.net.SocketServer;
import com.haxademic.sketch.robbie.Unity.UnityProcessingWebSocketSpout.App;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class UnityProcessingWebSocket
implements ISocketClientDelegate, IAppStoreListener {

	protected UnityProcessingWebSocketSpout p;
	protected PGraphics pg;
	
	protected boolean isServer = true;
	protected String socketServerAddress; // make null if we're running the server & client on the same machine, otherwise set ip e.g. "10.10.1.111"
	protected String LAST_MESSAGE = "";
	// AppStore keys
	protected String MOUSE_X = "MOUSE_X";
	protected String MOUSE_Y = "MOUSE_Y";
	
//	protected String UnityApp = "";
	protected String UnityApp = "D:\\Experiments\\Unity\\UnityProcessingWebSocketSpout\\Builds\\UnityProcessingWebSocketSpout.exe";
	protected int checkInTimeout = 1000;
	protected int lastCheckInTime;
	protected int UnityState;
	
	protected UnityProcessingSpout unitySpout;

	public UnityProcessingWebSocket() {
		p = (UnityProcessingWebSocketSpout) P.p;
		pg = p.pg;

		P.storeDistributed = AppStoreDistributed.instance();
		if(isServer == true) {
			P.storeDistributed.start(AppStoreDistributed.MODE_SERVER, null);
		} else {
			if(socketServerAddress != null) {
				P.out("Starting socket client, connecting to server at ", socketServerAddress);
				P.storeDistributed.start(AppStoreDistributed.MODE_CLIENT, socketServerAddress);
			} else {
				P.storeDistributed.start(AppStoreDistributed.MODE_CLIENT, P.storeDistributed.localSocketServerAddress());
			}
		}
		P.store.addListener(this);

		// Set to true to see messages coming in and out of the server
		// Must be set after server init
		SocketServer.DEBUG = false;
		
		// IMPORTANT: set default AppStore values to prevent crash
		P.store.setNumber(MOUSE_X, 0);
		P.store.setNumber(MOUSE_Y, 0);
		
		launchUnityApp();
	}
	
	protected void launchUnityApp() {
		if (UnityApp != "") {
			P.launch(UnityApp);
		}
		P.out("Launching Unity, waiting for WebSocket connection...");
	}
	
	
	public void drawPre(int frameCount) {}
	
	public void draw(int frameCount) {
		switch(UnityState) {
			// Not connected
			case 0:
				pg.background(0);
				pg.fill(255);
				pg.textAlign(P.CENTER);
				pg.text("Awaiting Unity WebSocket/Spout connection", pg.width/2, pg.height/2);
				break;	
			// Unity is online, send JSON, check timer
			case 1:
				sendMouse();
				checkIn();		
				if (unitySpout != null) unitySpout.drawSpout();			
				break;
			// Unity failed check-in, relaunch it
			case -1:
				UnityState = 0;
				launchUnityApp();
				break;
		}	
		P.store.showStoreValuesInDebugView();
	}
	
	protected void checkIn() {
		if (p.millis() > lastCheckInTime + checkInTimeout) {
			UnityState = -1;
			unitySpout.releaseSpout();
			unitySpout = null;
			P.out("Unity WebSocket offline");
		}
	}

	protected void sendMouse() {
		JSONObject jsonOut = new JSONObject();
		
		P.store.setNumber(MOUSE_X, p.mouseX);
		jsonOut.setInt("MOUSE_X", P.store.getInt(MOUSE_X));
		P.store.setNumber(MOUSE_Y, p.mouseY);
		jsonOut.setInt("MOUSE_Y", P.store.getInt(MOUSE_Y));
		
		P.storeDistributed.broadcastJson(jsonOut);
	}
	
	
	/////////////////////////////////////
	// ISocketClientDelegate delegate methods
	/////////////////////////////////////
	
	@Override
	public void messageReceived(String message) {}
	
	public void parseMessage(String message) {
		if (!message.equals(LAST_MESSAGE) ) {
			P.out(message);
			LAST_MESSAGE = message;
		}
		
		// Parse JSON data
		JSONObject eventData = JSONObject.parse(message);		
		int UnityWidth = eventData.getInt("UNITY_WIDTH");
		int UnityHeight = eventData.getInt("UNITY_HEIGHT");
		if (eventData.getBoolean("ONLINE")) {
			UnityState = 1;
			lastCheckInTime = p.millis();
			if (unitySpout == null) unitySpout = new UnityProcessingSpout(UnityWidth, UnityHeight, true, true);
		}
	}
	
	/////////////////////////////////////
	// AppStore listeners
	/////////////////////////////////////
	
	@Override
	public void updatedNumber(String key, Number val) {
		if(key.equals(App.ANIMATION_FRAME_PRE)) drawPre(val.intValue());
		if(key.equals(App.ANIMATION_FRAME)) draw(val.intValue());
	}
	public void updatedString(String key, String val) {
		if(key.equals(AppStoreDistributed.DATA_TYPE_JSON_KEY)) {
			parseMessage(val);
		}
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
