package com.haxademic.core.app;

import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PEvents;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.render.Renderer;
import com.haxademic.core.system.AppUtil;
import com.haxademic.core.ui.UITextInput;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PJOGL;

public class AppWindow
implements IAppStoreListener {
	
	protected boolean alwaysOnTop = false;
	protected int demoScreenshotFrame;
	protected boolean demoScreenshotDirty = false;

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

		initWindowAndRenderer();
		initAppSmoothing();
		setAppIcon();		
		setScreenDensity();
		setUpDemoScreenshot();

		// update every frame
		P.p.registerMethod(PRegisterableMethods.pre, this);
		P.p.registerMethod(PRegisterableMethods.post, this);
	}	
		
	public boolean alwaysOnTop() {
		return alwaysOnTop;
	}
	
	// initialize during settings() ----------

	protected void initWindowAndRenderer() {
		// get config
		P.renderer = Config.getString(AppSettings.RENDERER, P.P3D);
		boolean spanScreens = Config.getBoolean(AppSettings.SPAN_SCREENS, false);
		boolean fullScreen = Config.getBoolean(AppSettings.FULLSCREEN, false);
		boolean fillsScreen = Config.getBoolean(AppSettings.FILLS_SCREEN, false);
		int fullScreenScreenNumber = Config.getInt(AppSettings.FULLSCREEN_SCREEN_NUMBER, 1);
		int appW = Config.getInt(AppSettings.WIDTH, 800);
		int appH = Config.getInt(AppSettings.HEIGHT, 600);
		String pdfOutputFile = Config.getString(AppSettings.PDF_RENDERER_OUTPUT_FILE, "output/output.pdf");

		// opengl settings
		if (P.isOpenGL()) {
			PJOGL.profile = Config.getInt(AppSettings.PJOGL_PROFILE, 4);
		}
		
		// check fullscreen options, then move on to other renderers, with default being last case
		if (spanScreens == true) {
			P.p.fullScreen(P.renderer, P.SPAN);
		} else if (fullScreen == true) {
			if (fullScreenScreenNumber != 1)
				P.error("AppSettings.FULLSCREEN_SCREEN_NUMBER is busted if not screen #1. Use AppSettings.SCREEN_X, etc.");
			P.p.fullScreen(P.renderer);
		} else if (fillsScreen == true) {
			P.p.size(P.p.displayWidth, P.p.displayHeight, P.renderer);
		} else {
			if (P.renderer == PRenderers.PDF) { // headless output
				P.p.size(appW, appH, P.renderer, pdfOutputFile);
			} else {
				P.p.size(appW, appH, P.renderer);
			}
		}
	}

	protected void initAppSmoothing() {
		int smoothingLevel = Config.getInt(AppSettings.SMOOTHING, AppSettings.SMOOTH_MEDIUM);
		if (smoothingLevel == 0) {
			P.p.noSmooth();
		} else {
			P.p.smooth(smoothingLevel);
		}
	}

	protected void setAppIcon() {
		String appIconFile = Config.getString(AppSettings.APP_ICON, "haxademic/images/haxademic-logo.png");
		String iconPath = FileUtil.getPath(appIconFile);
		if (FileUtil.fileExists(iconPath)) {
			PJOGL.setIcon(iconPath);
		}
	}

	protected void setScreenDensity() {
		// DO WE DARE TRY THE RETINA SETTING?
		if (Config.getBoolean(AppSettings.RETINA, false) == true) {
			if (P.p.displayDensity() == 2) {
				P.p.pixelDensity(2);
			} else {
				P.error("Error: Attempting to set retina drawing on a non-retina screen");
			}
		}
	}
	
	protected void setUpDemoScreenshot() {
		demoScreenshotFrame = Config.getInt(AppSettings.RENDER_DEMO_SCREENSHOT_FRAME, 180);
	}
	
	// post setup() -------------------

	public void finishSetup() {
		// called from PAppletHax after other setup methods have run
		setFrameRate();
		setAppResizing();
	}

	protected void setFrameRate() {
		int fps = Config.getInt(AppSettings.FPS, 60);
		if(Config.getInt(AppSettings.FPS, 60) != 60) P.p.frameRate(fps);
	}

	protected void setAppResizing() {
		if(Config.getBoolean(AppSettings.RESIZABLE, true) == true) {
			AppUtil.setResizable(P.p, true);
		}
	}

	// during draw() ----------
	
	public void pre() {
		takeDemoScreenshot();
	}
	
	public void post() {
		updateAppTitle();
		checkFullscreenSettings();
	}
	
	protected void updateAppTitle() {
		if(P.renderer != PRenderers.PDF) {
			if(P.p.frameCount == 1) {
				AppUtil.setTitle(P.p, Config.getString(AppSettings.APP_NAME, "Haxademic | " + P.p.getClass().getSimpleName()));
			} else if(Config.getBoolean(AppSettings.SHOW_FPS_IN_TITLE, true)) {
				AppUtil.setTitle(P.p, Config.getString(AppSettings.APP_NAME, "Haxademic | " + P.p.getClass().getSimpleName()) + " | " + P.round(P.p.frameRate) + "fps");
			}
		}	
	}
	
	public void checkFullscreenSettings() {
		// move screen after first frame is rendered. this prevents weird issues (i.e. the app not even starting)
		if(P.p.frameCount == 10) {
			// check for additional screen_x params to manually place the window
			boolean isFullscreen = Config.getBoolean(AppSettings.FULLSCREEN, false);
			if(Config.getInt(AppSettings.SCREEN_X, -1) != -1) {
				if(isFullscreen == false) {
					P.error("Error: Manual screen positioning requires AppSettings.FULLSCREEN = true");
					return;
				}
				P.surface().setSize(Config.getInt(AppSettings.WIDTH, 800), Config.getInt(AppSettings.HEIGHT, 600));
				P.surface().setLocation(Config.getInt(AppSettings.SCREEN_X, 0), Config.getInt(AppSettings.SCREEN_Y, 0));  // location has to happen after size, to break it out of fullscreen
			}
			
			// Always on top?
			alwaysOnTop = Config.getBoolean(AppSettings.ALWAYS_ON_TOP, false);
			if(alwaysOnTop) AppUtil.setAlwaysOnTop(P.p, true);
		}
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

	public void takeDemoScreenshot() {
		boolean shouldTakeDemoScreenshot = P.p.frameCount == demoScreenshotFrame && Config.getBoolean(AppSettings.RENDER_DEMO_SCREENSHOT, true);
		if (demoScreenshotDirty || shouldTakeDemoScreenshot) {
			demoScreenshotDirty = false;
			// get classname and use it as the screenshot filename
			String className = P.appClassName();
			if (className.contains("Demo_")) {
				String outputFile = Renderer.saveDemoScreenshot(P.p.g, className);
				P.outInitLineBreak();
				P.outInit("Saved demo screenshot:", outputFile);
				P.outInitLineBreak();
			}
		}
	}

	////////////////////////
	// AppStore callbacks
	////////////////////////

	public void updatedNumber(String key, Number val) {}
	public void updatedString(String key, String val) {
		if(key.equals(PEvents.KEY_PRESSED) && !UITextInput.active()) {
			if(val.equals("F")) toggleAlwaysOnTop();
			if(val.equals("~")) demoScreenshotDirty = true;
		}
	}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}
