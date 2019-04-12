package com.haxademic.demo.data;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.store.AppStoreDistributed;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.net.SocketServer;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_AppStoreDistributed
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String MOUSE_X = "MOUSE_X";
	protected String MOUSE_Y = "MOUSE_Y";
	
	// use case config
	protected boolean isServer = false;
	protected String socketServerAddress = "10.10.1.111"; // null; // make null if we're running the server & client on the same machine
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	public void setupFirstFrame() {
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
		P.store.showStoreValuesInDebugView();

		// draw mouse position
		p.fill(255);
		p.noStroke();
		p.ellipse(P.store.getInt(MOUSE_X), P.store.getInt(MOUSE_Y), 20, 20);
	}
	
	protected void sendFrameMessage() {
		P.storeDistributed.setNumber("FRAME_COUNT", p.frameCount);
	}
	
	public void mouseClicked() {
		P.storeDistributed.setNumber("FRAME_CLICKED", p.frameCount);
	}
	
	/////////////////////////////////////////
	// AppStore callbacks
	/////////////////////////////////////////

	@Override
	public void updatedNumber(String key, Number val) {
//		p.debugView.setValue(key, val.floatValue());
	}

	@Override
	public void updatedString(String key, String val) {
//		p.debugView.setValue(key, val);
	}

	@Override
	public void updatedBoolean(String key, Boolean val) {
//		p.debugView.setValue(key, val);
	}	

	public void updatedImage(String key, PImage val) {
		
	}
	
	public void updatedBuffer(String key, PGraphics val) {
		
	}


}

