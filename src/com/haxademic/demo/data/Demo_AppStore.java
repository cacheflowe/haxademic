package com.haxademic.demo.data;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_AppStore
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true );
	}

	protected void firstFrame() {
		P.store.addListener(this);
	}
	
	protected void drawApp() {
		background(0);
		P.store.setNumber("frameCount", p.frameCount);
		P.store.setBoolean("Frame over 100", (p.frameCount % 200) > 100);
		P.store.setNumber("mousePercentX", Mouse.xNorm);
		P.store.setNumber("mousePercentY", Mouse.yNorm);
		P.store.setImage("image", DemoAssets.justin());
		
		P.store.setBoolean("has doesntExist?", P.store.hasBoolean("doesntExist"));
		P.store.getBoolean("doesntExist", false);
		// for (int i = 0; i < 50; i++) { P.store.setNumber("test_"+i, i); }
		P.store.showStoreValuesInDebugView();
		
		// draw image from buffer
		p.image(P.store.getImage("image"), p.width * P.store.getFloat("mousePercentX"), p.height * P.store.getFloat("mousePercentY"));
	}

	//////////////////////////////////////////////
	// IAppStoreListener delegate methods
	//////////////////////////////////////////////
	
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

