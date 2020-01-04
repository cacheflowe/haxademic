package com.haxademic.core.app;

import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.AppUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PJOGL;

public class AppWindow
implements IAppStoreListener {
	
	protected boolean alwaysOnTop = false;

	// Singleton instance
	
	public static AppWindow instance;
	
	public static AppWindow instance() {
		if(instance != null) return instance;
		instance = new AppWindow();
		return instance;
	}
	
	// Constructor

	public AppWindow() {
		P.store.addListener(this);
		buildAppWindow(P.p);
		// update every frame
		P.p.registerMethod(PRegisterableMethods.pre, this);
		P.p.registerMethod(PRegisterableMethods.post, this);
	}	
		
	public boolean alwaysOnTop() {
		return alwaysOnTop;
	}

	
	protected void buildAppWindow(PApplet p) {
		// SELECT RENDERER AND WINDOW SIZE
		PJOGL.profile = 4;
		if(Config.getBoolean(AppSettings.SPAN_SCREENS, false) == true) {
			// run fullscreen across all screens
			p.fullScreen(P.renderer, P.SPAN);
		} else if(Config.getBoolean(AppSettings.FULLSCREEN, false) == true) {
			// run fullscreen - default to screen #1 unless another is specified
			if(Config.getInt(AppSettings.FULLSCREEN_SCREEN_NUMBER, 1) != 1) DebugUtil.printErr("AppSettings.FULLSCREEN_SCREEN_NUMBER is busted if not screen #1. Use AppSettings.SCREEN_X, etc.");
			p.fullScreen(P.renderer); // , Config.getInt(AppSettings.FULLSCREEN_SCREEN_NUMBER, 1)
		} else if(Config.getBoolean(AppSettings.FILLS_SCREEN, false) == true) {
			// fills the screen, but not fullscreen
			p.size(p.displayWidth, p.displayHeight, P.renderer);
		} else {
			if(P.renderer == PRenderers.PDF) {
				// set headless pdf output file
				p.size(Config.getInt(AppSettings.WIDTH, 800),Config.getInt(AppSettings.HEIGHT, 600), P.renderer, Config.getString(AppSettings.PDF_RENDERER_OUTPUT_FILE, "output/output.pdf"));
			} else {
				// run normal P3D renderer
				p.size(Config.getInt(AppSettings.WIDTH, 800),Config.getInt(AppSettings.HEIGHT, 600), P.renderer);
			}
		}
		
		// SMOOTHING
		if(Config.getInt(AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH) == 0) {
			p.noSmooth();
		} else {
			p.smooth(Config.getInt(AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH));	
		}

		// DO WE DARE TRY THE RETINA SETTING?
		if(Config.getBoolean(AppSettings.RETINA, false) == true) {
			if(p.displayDensity() == 2) {
				p.pixelDensity(2);
			} else {
				DebugUtil.printErr("Error: Attempting to set retina drawing on a non-retina screen");
			}
		}	
	}
	
	public void finishSetup() {
		// FRAMERATE
		int _fps = Config.getInt(AppSettings.FPS, 60);
		if(Config.getInt(AppSettings.FPS, 60) != 60) P.p.frameRate(_fps);
		
		// SET APP ICON
		String appIconFile = Config.getString(AppSettings.APP_ICON, "haxademic/images/haxademic-logo.png");
		String iconPath = FileUtil.getPath(appIconFile);
		if(FileUtil.fileExists(iconPath)) {
			PJOGL.setIcon(iconPath);
		}
	}
	
	protected void updateAppTitle() {
		if(P.renderer != PRenderers.PDF) {
			if(P.p.frameCount == 1) {
				AppUtil.setTitle(P.p, Config.getString(AppSettings.APP_NAME, "Haxademic | " + P.p.getClass().getSimpleName()));
			} else if(Config.getBoolean(AppSettings.SHOW_FPS_IN_TITLE, false)) {
				AppUtil.setTitle(P.p, Config.getString(AppSettings.APP_NAME, "Haxademic | " + P.p.getClass().getSimpleName()) + " | " + P.round(P.p.frameRate) + "fps");
			}
		}	
	}
	
	public void checkFullscreenSettings() {
		boolean isFullscreen = Config.getBoolean(AppSettings.FULLSCREEN, false);
		// check for additional screen_x params to manually place the window
		if(Config.getInt("screen_x", -1) != -1) {
			if(isFullscreen == false) {
				DebugUtil.printErr("Error: Manual screen positioning requires AppSettings.FULLSCREEN = true");
				return;
			}
			P.surface().setSize(Config.getInt(AppSettings.WIDTH, 800), Config.getInt(AppSettings.HEIGHT, 600));
			P.surface().setLocation(Config.getInt(AppSettings.SCREEN_X, 0), Config.getInt(AppSettings.SCREEN_Y, 0));  // location has to happen after size, to break it out of fullscreen
		}
		
		// Always on top?
		alwaysOnTop = Config.getBoolean(AppSettings.ALWAYS_ON_TOP, false);
		if(alwaysOnTop) AppUtil.setAlwaysOnTop(P.p, true);
	}

	protected void keepOnTop() {
		if(alwaysOnTop == true) {
			if(P.p.frameCount % 600 == 0) AppUtil.requestForegroundSafe();
		}
	}

	protected void toggleAlwaysOnTop() {
		alwaysOnTop = !alwaysOnTop;
		AppUtil.setAlwaysOnTop(P.p, alwaysOnTop);
	}
	
	// Frame updates
	
	public void pre() {
	}
	
	public void post() {
		updateAppTitle();
		if(P.p.frameCount == 10) {
			// move screen after first frame is rendered. this prevents weird issues (i.e. the app not even starting)
			checkFullscreenSettings();
		}
		// keepOnTop();
	}


	public void updatedNumber(String key, Number val) {
	}
	public void updatedString(String key, String val) {
		if(key.equals(PEvents.KEY_PRESSED) && val.equals("F")) {
			toggleAlwaysOnTop();
		}
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}
