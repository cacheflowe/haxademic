package com.haxademic.sketch.robbie.joyride;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.sketch.robbie.joyride.video.VideoTexture;

import processing.core.PGraphics;
import processing.core.PImage;

public class JoyrideSF 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected VideoTexture videoTexture;
	
	protected int compW = 1920;
	protected int compH = 2160;
	protected float appScale = 0.4f;
	
	
	/////////////////////////////////
	// INIT
	/////////////////////////////////
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, (int)(compW * appScale) );
		p.appConfig.setProperty( AppSettings.HEIGHT, (int)(compH * appScale) );
		p.appConfig.setProperty( AppSettings.PG_WIDTH, compW );
		p.appConfig.setProperty( AppSettings.PG_HEIGHT, compH );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false );
		p.appConfig.setProperty( AppSettings.APP_NAME, "JoyrideSF" );
		p.appConfig.setProperty( AppSettings.APP_ICON, "images/app-icon.png" );
	}
		
	public void setupFirstFrame() {
		p.background(0);
		p.noFill();
		p.noStroke();
		
//		pg = PG.newPG(compW, compH, false, false);
		videoTexture = new VideoTexture();
	}

	public void drawApp() {
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
		
		// post draw updates
		P.store.setNumber(App.ANIMATION_FRAME_POST, p.frameCount);
		
		// draw to screen

		ImageUtil.cropFillCopyImage(pg, p.g, false);	// for testing, show full view in app window
//		PG.setDrawCorner(pg);
//		PG.setCenterScreen(pg);
//		p.image(pg, 0, 0);								// on LED panels, draw from top left? or
//		p.image(pg, 0, 0, 1920, 1080);				// use this if we end up scaling up from 1080p

	}
	
	
	/////////////////////////////////
	// APPSTORE LISTENERS
	/////////////////////////////////
	
	public void updatedNumber(String key, Number val) {
		if(key.equals(App.APP_STATE)) {
			//P.println("New App State", val);
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
	public static final String ANIMATION_FRAME_AFTER_DRAW = "ANIMATION_FRAME_AFTER_DRAW";
	
	public static final String CYCLE_TEXTURE = "CYCLE_TEXTURE";
	
	}
	

}
