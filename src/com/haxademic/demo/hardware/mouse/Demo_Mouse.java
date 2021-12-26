package com.haxademic.demo.hardware.mouse;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.mouse.Mouse;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_Mouse
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setAppSize(800, 600);
		Config.setProperty( AppSettings.APP_NAME, "Demo_Mouse" );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
//		Config.setProperty(AppSettings.RENDERER, PRenderers.JAVA2D);
	}

	protected void firstFrame() {
		P.store.addListener(this);
	}

	protected void drawApp() {
		p.background(0);
		
		// click mouse & move
		if(p.frameCount % 600 == 0) {
//			p.noCursor();
			Mouse.mouseClickAt(300, 300);
			Mouse.movePointerTo(99999, 0);
		}
		if(p.frameCount % 60 == 0) {
			Mouse.movePointerTo(p.frameCount % 1920, p.frameCount % 1080);
		}
		Mouse.movePointerTo(500 + P.round(100f * P.cos(p.frameCount/10f)), 500 + P.round(100f * P.sin(p.frameCount/10f)));
		
		DebugView.setValue("Mouse.mouseShowing()", Mouse.isShowing());
	}

	/////////////////////////////////////
	// AppStore listeners
	/////////////////////////////////////

	@Override
	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {
		if(key.equals(PEvents.MOUSE_CLICKED)) P.out("click!");
	}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}


}