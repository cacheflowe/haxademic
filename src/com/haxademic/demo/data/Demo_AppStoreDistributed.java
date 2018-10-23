package com.haxademic.demo.data;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.data.store.AppStoreDistributed;
import com.haxademic.core.data.store.IAppStoreListener;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_AppStoreDistributed
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty(AppSettings.WIDTH, 400 );
	}

	public void setupFirstFrame() {
		P.storeDistributed = AppStoreDistributed.instance();
		P.storeDistributed.startServer();
		P.storeDistributed.startClient(null);
		P.store.addListener(this);
	}
	
	public void drawApp() {
		background(0);
		if(p.mouseX != p.pmouseX) P.storeDistributed.setNumber("mousePercentX()", p.mousePercentX());
		if(p.mouseY != p.pmouseY) P.storeDistributed.setNumber("mousePercentY()", p.mousePercentY());
		if(p.frameCount % 100 == 0) sendFrameMessage(); 
		P.store.showStoreValuesInDebugView();
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

