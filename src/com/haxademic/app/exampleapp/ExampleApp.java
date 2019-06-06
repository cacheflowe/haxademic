
package com.haxademic.app.exampleapp;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.shared.InputTrigger;

import processing.core.PGraphics;
import processing.core.PImage;

public class ExampleApp
extends PAppletHax
implements IAppStoreListener {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/////////////////////////////////
	// PROPERTIES
	/////////////////////////////////
	
	protected InputTrigger trigger1 = (new InputTrigger()).addKeyCodes(new char[]{'1'})
			  											  .addGamepadControls(new String[]{"Button 9"});

	/////////////////////////////////
	// INIT
	/////////////////////////////////
	
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
		P.store.addListener(this);
		
		// build screens / objects
//		backgroundColor = new BackgroundColor();
	}	
	
	protected void addKeyCommandInfo() {
		super.addKeyCommandInfo();
		p.debugView.setHelpLine("\n" + DebugView.TITLE_PREFIX + "Custom Key Commands", "");
		p.debugView.setHelpLine("[1] |", "Trigger");
	}
	
	/////////////////////////////////
	// STATE
	/////////////////////////////////
	
	public void setState(int newState ) {
		P.store.setNumber(App.QUEUED_APP_STATE, newState);
	}
	
	public int getState() {
		return P.store.getInt(App.APP_STATE);
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
		// update state
		checkInputs();
		checkQueuedState();
		
		// main app canvas context setup
		p.background(0);
		p.noStroke();
		PG.setDrawCorner(p);

		// MAIN DRAW STEPS:
		// 1. update offscreen buffers before main drawing w/ANIMATION_FRAME_PRE
		// 2. draw into main buffer w/ANIMATION_FRAME
		// 3. draw main buffer to screen
		P.store.setNumber(App.ANIMATION_FRAME_PRE, p.frameCount);
		pg.beginDraw();
		P.store.setNumber(App.ANIMATION_FRAME, p.frameCount);
		pg.endDraw();
		ImageUtil.cropFillCopyImage(pg, p.g, false);
		
		// post draw updates
		P.store.setNumber(App.ANIMATION_FRAME_POST, p.frameCount);

		// debug
		P.store.showStoreValuesInDebugView();
	}
	
	/////////////////////////////////
	// APPSTORE LISTENERS
	/////////////////////////////////

	public void updatedNumber(String key, Number val) {
		if(key.equals(App.APP_STATE)) {
//			P.println("New App State", val);
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
	
	/////////////////////////////////
	// APP CONFIG & EVENTS & CONSTANTS
	/////////////////////////////////

	public class App {
		
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
		
		// events
		
		public static final String ANIMATION_FRAME_PRE = "ANIMATION_FRAME_PRE";
		public static final String ANIMATION_FRAME = "ANIMATION_FRAME";
		public static final String ANIMATION_FRAME_POST = "ANIMATION_FRAME_POST";

	}

}
