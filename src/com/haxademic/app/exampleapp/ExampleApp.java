
package com.haxademic.app.exampleapp;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.shared.InputTrigger;

import processing.core.PGraphics;
import processing.core.PImage;

public class ExampleApp
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected InputTrigger trigger1 = new InputTrigger(new char[]{'1'}, null, new Integer[]{41}, null, null);

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.APP_NAME, "Example App" );
		p.appConfig.setProperty( AppSettings.APP_ICON, "images/app-icon.png" );
	}
	
	public void setupFirstFrame() {
		// init state
		P.store.setNumber(App.APP_STATE, App.APP_STATE_INTRO);
		P.store.setNumber(App.QUEUED_APP_STATE, App.APP_STATE_NONE);
		P.store.setNumber(App.ANIMATION_FRAME, 0);
		P.store.setNumber(App.ANIMATION_FRAME_PRE, 0);
		P.store.setNumber(App.SECONDS_LEFT, 0);
		P.store.addListener(this);
		
		// build screens / objects
//		backgroundColor = new BackgroundColor();
		
		// add help text
		addHelpText();
	}	
	
	protected void addHelpText() {
		p.debugView.setHelpLine("__ Key Commands", "__\n");
		p.debugView.setHelpLine("ESC |", "Quit");
	}
	
	/////////////////////////////////
	// STATE
	/////////////////////////////////
	
	public void setState(int newState ) {
		P.store.setNumber(App.QUEUED_APP_STATE, newState);
	}
	
	protected void checkQueuedState() {
		int queuedState = P.store.getNumber(App.QUEUED_APP_STATE).intValue();
		if(queuedState != App.APP_STATE_NONE) {
			P.store.setNumber(App.APP_STATE, queuedState);
			P.store.setNumber(App.QUEUED_APP_STATE, App.APP_STATE_NONE);
		}
	}
		
	/////////////////////////////////
	// INPUT
	/////////////////////////////////
	
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == '.') P.store.setString(App.SAVE_IMAGE, "");
	}
	
	protected void checkInputs() {
//		if(trigger1.triggered()) sequencers[0].trigger(true);
	}
	
	/////////////////////////////////
	// DRAW
	/////////////////////////////////
	
	public void drawApp() {
		// context setup
		p.background(0);
		p.noStroke();
		DrawUtil.setDrawCorner(p);

		// update state & offscreen buffers before main drawing
		checkQueuedState();
		P.store.setNumber(App.ANIMATION_FRAME_PRE, p.frameCount);
		
		pg.beginDraw();
		checkInputs();
		P.store.setNumber(App.ANIMATION_FRAME, p.frameCount);
		pg.endDraw();
		
		// draw buffer
		ImageUtil.cropFillCopyImage(pg, p.g, false);
		
		// debug
		p.debugView.setValue("APP_STATE", P.store.getInt(App.APP_STATE));
	}
	
	/////////////////////////////////
	// APPSTORE LISTENERS
	/////////////////////////////////

	@Override
	public void updatedNumber(String key, Number val) {
		if(key.equals(App.APP_STATE)) {
//			P.println("New App State", val);
		}
	}

	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	
	public class App {
		
		// prefs
		
		public static final String fontHeavyItalic = "fonts/AvenirNext-HeavyItalic.ttf";

		// config
		
		public static final int PLAY_TIME = 90;
		
		// state
		
		public static final String APP_STATE = "APP_STATE";
		public static final String QUEUED_APP_STATE = "QUEUED_APP_STATE";
		public static final int APP_STATE_INTRO = 0;
		public static final int APP_STATE_MENU = 1;
		public static final int APP_STATE_PLAY = 2;
		public static final int APP_STATE_READY = 3;
		public static final int APP_STATE_GAME_OVER = 4;
		public static final int APP_STATE_NONE = -1;
		
		public static final String SECONDS_LEFT = "SECONDS_LEFT";
		
		// events
		
		public static final String ANIMATION_FRAME_PRE = "ANIMATION_FRAME_PRE";
		public static final String ANIMATION_FRAME = "ANIMATION_FRAME";

	}

}
