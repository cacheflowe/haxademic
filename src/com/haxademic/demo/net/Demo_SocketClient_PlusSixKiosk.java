package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.net.PlusSixKiosk;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.data.StringList;

public class Demo_SocketClient_PlusSixKiosk
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	

	// Kiosk URLs, QR, and authentication
	// TODO: Add switches for testing locally
	protected String urlWs = "ws://localhost:3001";
	protected String urlUi = "http://localhost:3000";
	protected String accId = "e448b1bb-a0db-4db9-90c8-55db9c7ec568";
	protected String accKey = "da71f60a-bacb-4666-aa26-4b7d36d4eed3";

	protected PlusSixKiosk plusSixKiosk;
	
	protected void config() {
		Config.setAppSize(1280, 720);
		Config.setProperty(AppSettings.APP_NAME, "SocketClient");
	}
	
	protected void firstFrame() {
		plusSixKiosk = new PlusSixKiosk(accId, accKey, urlWs, urlUi);
		plusSixKiosk.setColors(0xff444444, 0xff00ffff);
		plusSixKiosk.connectToSystemChannel(); // optional! adds AppStoreDistributed on ws server
		setCustomConfig();
		P.store.addListener(this);
	}
	
	public void setCustomConfig() {
		// todo: move this to parent class
		// and add the current viz that's displaying - this should update frequently
		// TODO: config object should be passed in from the outer app, should be updatable, and should send each time there's a connection
		// TODO: as soon as kiosk connection is made, we should disconnect from the timer, so the viz doesn't change
		// custom info per app
		String[] availableVizIds = new String[] {"heatmap", "grooves", "numbers", "tides"};
		String activeViz = "heatmap";
		
		// set props on config object that's sent to UI on connection 
		JSONObject configObject = plusSixKiosk.getSessionConfigJson();
		JSONArray availableViz = new JSONArray(new StringList(availableVizIds));
		configObject.setJSONArray("availableViz", availableViz);
		configObject.setString("activeViz", activeViz);
	}
	
	protected void drawApp() {
		background(0);
		updatePlusSix();
		if(!plusSixKiosk.touchpadIsActive() && plusSixKiosk.socketIsConnected()) {
			p.image(plusSixKiosk.qrImage(), 0, p.height - plusSixKiosk.qrImage().height);
		}
		
		// test sync between machines
		if(KeyboardState.keyTriggered('q')) {
			P.storeDistributed.setNumber("mouseX", Mouse.x);
		}
		P.store.showStoreValuesInDebugView();
	}
	
	protected void updatePlusSix() {
		plusSixKiosk.update();
		plusSixKiosk.drawKioskDebug(p.g);
	}
	
	///////////////////////////////////////////
	// IAppStoreListener callbacks
	///////////////////////////////////////////
	
	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {
		if(key.equals(PlusSixKiosk.TOUCHPAD_IS_CONNECTED)) {
			P.out("CONNECTED?!?!", val);
		}
	}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}


}
