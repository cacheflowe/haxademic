package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.store.IAppStoreListener;
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
	
	public void firstFrame() {
		// basic web server
		// server = new WebServer(new UIControlsHandler(), false);
		
		// basic web server with SSL
		server = new WebServer(new UIControlsHandler(), false, true);
	}
	
	public void drawApp() {
		background(0);
		if(p.frameCount == 200) SystemUtil.openWebPage(WebServer.getServerAddress() + "web-server-demo/");
		// draw slider val
		p.fill(255);
		p.rect(0, 0, P.map(HttpInputState.instance().getValue("slider1"), 0, 1, 0, p.width), p.height);
		
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
