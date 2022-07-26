package com.haxademic.demo.app.example;

import com.haxademic.core.app.P;
import com.haxademic.core.data.store.AppState;
import com.haxademic.core.data.store.IAppStoreListener;

import processing.core.PGraphics;
import processing.core.PImage;

public class _ExampleObject
implements IAppStoreListener {

	protected ExampleApp p;
	protected PGraphics pg;
	
	public _ExampleObject() {
		p = (ExampleApp) P.p;
		pg = p.pg;
		P.store.addListener(this);
	}
	
	protected void drawPre(int frameCount) {
	}

	protected void draw(int frameCount) {
		
	}
	
	/////////////////////////////////////
	// AppStore listeners
	/////////////////////////////////////

	@Override
	public void updatedNumber(String key, Number val) {
		if(key.equals(AppState.ANIMATION_FRAME)) draw(val.intValue());
		if(key.equals(AppState.ANIMATION_FRAME_PRE)) drawPre(val.intValue());
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

}
