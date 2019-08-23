package com.haxademic.sketch.robbie.NikeGenerator;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.sketch.robbie.NikeGenerator.NikeGenerator.App;

import processing.core.PGraphics;
import processing.core.PImage;

public class DrawObjects
implements IAppStoreListener {
	
	protected NikeGenerator p;
	protected PGraphics pg;
	
	protected InputTrigger key1 = (new InputTrigger()).addKeyCodes(new char[]{'1'});
	
	ArrayList<Grid> bgGridPool = new ArrayList<Grid>();
	protected int bgGrid = 1;
	ArrayList<Grid> fgGridPool = new ArrayList<Grid>();
	protected int fgGrid = 1;

	


	protected String fontHOI ="haxademic/fonts/NeueHelveticaHOI.otf";
	
	public DrawObjects() {
		p = (NikeGenerator) P.p;
		pg = p.pg;
		P.store.addListener(this);
		
		bgGridPool.add(new Grid(p.width/16, p.color(128), 1, 10));
		bgGridPool.add(new Grid(p.width/4, p.color(128), 1));
		
		fgGridPool.add(new Grid(p.width/8, p.color(128, 0, 0), 1, 10));
		fgGridPool.add(new Grid(p.width/2, p.color(128, 0, 0), 1));
	}
	
	public void drawPre(int frameCount) {}
	
	public void draw(int frameCount) {
		if(p.frameCount % 10 == 0) updateObjects(); 
		if(key1.triggered()) {
			updateObjects();
		}
		
		switch (bgGrid) {
			case 0:
				break;
			case 1:
				bgGridPool.get(0).draw(pg, 0, 0, 16, 16);
				break;
			case 2:
				bgGridPool.get(1).draw(pg, 0, 0, 2, 2);
				break;
		}
		
		switch (fgGrid) {
			case 0:
				break;
			case 1:
				fgGridPool.get(0).draw(pg, 0, 0, 6, 6);
				break;
			case 2:
				fgGridPool.get(1).draw(pg, 0, 0, 2, 2);
				break;
		}
		
		
		FontCacher.setFontOnContext(pg, FontCacher.getFont(fontHOI, 30), p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		pg.text("TEST", 20, 20);

	}
	
	public void updateObjects() {
		bgGridUpdate(); 
		fgGridUpdate();
		P.out(bgGrid + ", " + fgGrid);
	}
	
	public void bgGridUpdate() {
		// increment
		bgGrid = (bgGrid >= 2) ? 0 : bgGrid+1;
		// random
		bgGrid = (int)p.random(bgGridPool.size()+1);
	}

	public void fgGridUpdate() {
		// increment
		fgGrid = (fgGrid >= 2) ? 0 : fgGrid+1;
		// random
		fgGrid = (int)p.random(fgGridPool.size()+1);
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
