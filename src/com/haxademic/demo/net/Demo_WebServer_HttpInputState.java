package com.haxademic.demo.net;

import com.cage.zxing4p3.ZXING4P;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.net.UIControlsHandler;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.system.SystemUtil;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_WebServer_HttpInputState
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected WebServer server;
	protected String webServerURL;
	protected boolean isSSL = false;
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
	}

	protected void firstFrame() {
		P.out("isSSL", isSSL);
		if(!isSSL) {
			server = new WebServer(new UIControlsHandler(), false);
			webServerURL = WebServer.getServerAddress() + "web-server-demo/";
		} else {
			server = new WebServer(new UIControlsHandler(), false, true);
			webServerURL = "https://localhost/web-server-demo/"; // don't use IP address because certificate is for localhost
		}
		// set custom server on HttpInputState or else it'll create its own!
		HttpInputState.instance(server);
		
		// create QR code
		ZXING4P qr = new ZXING4P();
		PImage qrImage = qr.generateQRCode(webServerURL, 128, 128);
		DebugView.setTexture("qrImage", qrImage);
	}
	
	protected void drawApp() {
		background(0);
		if(p.frameCount == 200) SystemUtil.openWebPage(webServerURL);

		// draw slider val, but wait a moment, because HttpInputState.instance() was creating a second webserver before the thread created the first!!!
		if (p.frameCount > 200) {
			p.fill(255);
			p.rect(0, 0, P.map(HttpInputState.instance().getValue("slider1"), 0, 1, 0, p.width), p.height);
		}
		
		// show incoming web request paths in DebugView
		P.store.showStoreValuesInDebugView();
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'r') server.restart();
		if(p.key == 's') server.stop();
	}
	
	// AppStore listeners

	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	
}
