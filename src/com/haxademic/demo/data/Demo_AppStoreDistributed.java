package com.haxademic.demo.data;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.AppStoreDistributed;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.net.SocketServer;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONObject;

public class Demo_AppStoreDistributed
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String MOUSE_X = "MOUSE_X";
	protected String MOUSE_Y = "MOUSE_Y";
	
	// use case config
	protected boolean isServer = true;
	protected String socketServerAddress = "ws://10.10.1.111:3001"; // null; // make null if we're running the server & client on the same machine
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	public void firstFrame() {
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

		// set to true to see messages coming in and out of the server
		// must be set after server init
		SocketServer.DEBUG = false;
		
		// default AppStore values to prevent crash
		P.store.setNumber(MOUSE_X, 0);
		P.store.setNumber(MOUSE_Y, 0);
	}
	
	public void drawApp() {
		background(0);
		if(p.mouseX != p.pmouseX) P.storeDistributed.setNumber(MOUSE_X, p.mouseX);
		if(p.mouseY != p.pmouseY) P.storeDistributed.setNumber(MOUSE_Y, p.mouseY);
		if(p.frameCount % 100 == 0) sendFrameMessage(); 
		if(p.frameCount % 200 == 0) broadcastJson(); 
		P.store.showStoreValuesInDebugView();

		// draw mouse position
		p.fill(255);
		p.noStroke();
		p.ellipse(P.store.getInt(MOUSE_X), P.store.getInt(MOUSE_Y), 20, 20);
	}
	
	protected void sendFrameMessage() {
		P.storeDistributed.setNumber("FRAME_COUNT", p.frameCount);
	}
	
	protected void broadcastJson() {
	    JSONObject jsonOut = new JSONObject();
	    jsonOut.setBoolean("data", false);
	    jsonOut.setString("test", "test");
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
//		DebugView.setValue(key, val);
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

