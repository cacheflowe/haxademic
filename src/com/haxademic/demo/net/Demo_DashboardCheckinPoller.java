package com.haxademic.demo.net;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.net.DashboardCheckinPoller;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_DashboardCheckinPoller
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DashboardCheckinPoller dashboardPoster;
	
	protected void firstFrame() {
		DashboardCheckinPoller.DEBUG = true;
		DashboardCheckinPoller.JPG_QUALITY = 0.85f;
		dashboardPoster = new DashboardCheckinPoller("test-app-3", "TEST APP 3", "http://localhost/haxademic/www/dashboard-new/", 5, 10, 0.5f);
		dashboardPoster.setCustomImage(p.g, 20);
		
		// listen for updates
		P.store.addListener(this);
	}
	
	protected void drawApp() {
		// background
		pg.beginDraw();
		PG.setDrawCenter(pg);
		pg.background((float)mouseX/width * 255,(float)mouseY/height * 255,0);
		pg.fill(255);
		
		// square
		pg.pushMatrix();
		pg.translate(p.width/2, p.height/2);
		pg.rotate(p.frameCount * 0.01f);
		pg.rect(0, 0, 100, 100);
		pg.popMatrix();
		
		// draw to screen
		pg.endDraw();
		p.image(pg, 0, 0);
	
		// add custom debug data 
		dashboardPoster.setCustomValue("mouseX", ""+p.mouseX);
		dashboardPoster.setCustomValue("mouseY", p.mouseY);
 	}
	
	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {
		DebugView.setValue(key, val);
		if(key.equals(DashboardCheckinPoller.IMAGE_READY_SCREENSHOT_64)) P.out("NEW [IMAGE_READY_SCREENSHOT_64]");
		if(key.equals(DashboardCheckinPoller.IMAGE_READY_CUSTOM_64)) P.out("NEW [IMAGE_READY_CUSTOM_64]");
	}
	public void updatedBoolean(String key, Boolean val) {}	
	public void updatedImage(String key, PImage val) {
		DebugView.setTexture(key, val);
	}
	public void updatedBuffer(String key, PGraphics val) {
		DebugView.setTexture(key, val);
	}

	
}
