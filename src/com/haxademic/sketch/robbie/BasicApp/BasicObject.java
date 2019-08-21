package com.haxademic.sketch.robbie.BasicApp;

import com.haxademic.core.app.P;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.sketch.robbie.BasicApp.BasicApp.App;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.MouseEvent;

public class BasicObject
implements IAppStoreListener {
	
	protected BasicApp p;
	protected PGraphics pg;
	
	protected InputTrigger key1 = (new InputTrigger()).addKeyCodes(new char[]{'1'});
	
	public BasicObject() {
		p = (BasicApp) P.p;
		pg = p.pg;
//		P.p.registerMethod("mouseEvent", this);
		P.store.addListener(this);
	}
	
	public void drawPre(int frameCount) {}
	
	public void draw(int frameCount) {
		pg.background(100);
//		if(key1.triggered()) pg.background(255,0,0);
		if(key1.on()) pg.background(0,0,255);
	}
	
	/////////////////////////////////////////
	// Mouse listener
	/////////////////////////////////////////
	
	public void mouseEvent(MouseEvent event) {
		//mouseX = event.getX();
		//mouseY = event.getY();
		
		switch (event.getAction()) {
			case MouseEvent.ENTER:
				break;
			case MouseEvent.MOVE:
				break;
			case MouseEvent.EXIT:
				break;
			case MouseEvent.PRESS:
				break;
			case MouseEvent.RELEASE:
				break;
		}
	}

	/////////////////////////////////////
	// AppStore listeners
	/////////////////////////////////////
	
	@Override
	public void updatedNumber(String key, Number val) {
	if(key.equals(App.ANIMATION_FRAME_PRE)) drawPre(val.intValue());
	if(key.equals(App.ANIMATION_FRAME)) draw(val.intValue());
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}
