
package com.haxademic.demo.app.example;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.AppState;
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
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	/////////////////////////////////
	// PROPERTIES
	/////////////////////////////////
	
	protected InputTrigger trigger1 = (new InputTrigger()).addKeyCodes(new char[]{'1'}).addGamepadControls(new String[]{"Button 9"});
	protected InputTrigger trigger2 = (new InputTrigger()).addKeyCodes(new char[]{'2'}).addGamepadControls(new String[]{"Button 10"});

	/////////////////////////////////
	// INIT
	/////////////////////////////////
	
	protected void config() {
		Config.setProperty( AppSettings.APP_NAME, "Example App" );
		Config.setProperty( AppSettings.APP_ICON, "images/app-icon.png" );
	}
	
	protected void firstFrame() {
		// init state
		AppState.init(App.APP_STATE_INTRO);
		P.store.addListener(this);
		
		// build screens / objects
//		backgroundColor = new BackgroundColor();
		
		addKeyCommandInfo();
	}	
	
	protected void addKeyCommandInfo() {
		DebugView.setHelpLine(DebugView.TITLE_PREFIX + "Custom Key Commands", "");
		DebugView.setHelpLine("[1] |", "Trigger");
	}
	
	/////////////////////////////////
	// INPUT
	/////////////////////////////////
	
	public void keyPressed() {
		super.keyPressed();
//		if(p.key == '.') P.store.setString(App.SAVE_IMAGE, "");
	}
	
	protected void checkInputs() {
		if(trigger1.triggered()) AppState.set(App.APP_STATE_INTRO);
		if(trigger2.triggered()) AppState.set(App.APP_STATE_PLAY);
	}
	
	/////////////////////////////////
	// DRAW
	/////////////////////////////////
	
	protected void drawApp() {
		// update state
		checkInputs();
		AppState.checkQueuedState();
		
		// main app canvas context setup
		p.background(0);
		p.noStroke();
		PG.setDrawCorner(p);

		// MAIN DRAW STEPS:
		// 1. update offscreen buffers before main drawing w/ANIMATION_FRAME_PRE
		// 2. draw into main buffer w/ANIMATION_FRAME
		// 3. draw main buffer to screen
		P.store.setNumber(AppState.ANIMATION_FRAME_PRE, p.frameCount);
		pg.beginDraw();
		pg.background(0);
		P.store.setNumber(AppState.ANIMATION_FRAME, p.frameCount);
		pg.endDraw();
		ImageUtil.cropFillCopyImage(pg, p.g, false);
		
		// post draw updates
		P.store.setNumber(AppState.ANIMATION_FRAME_POST, p.frameCount);

		// debug
		P.store.showStoreValuesInDebugView();
	}
	
	/////////////////////////////////
	// APPSTORE LISTENERS
	/////////////////////////////////

	public void updatedNumber(String key, Number val) {
		if(key.equals(AppState.APP_STATE)) {
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
		
		public static final String APP_STATE_INTRO = "APP_STATE_INTRO";
		public static final String APP_STATE_MENU = "APP_STATE_MENU";
		public static final String APP_STATE_PLAY = "APP_STATE_PLAY";
		public static final String APP_STATE_READY = "APP_STATE_READY";
		public static final String APP_STATE_GAME_OVER = "APP_STATE_GAME_OVER";
		public static final String APP_STATE_NONE = "APP_STATE_NONE";
		
	}

}
