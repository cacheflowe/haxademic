package com.haxademic.demo.app;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.system.AppUtil;

public class Demo_PAppletHax_ScreenSizeFullscreenOptions
extends PAppletHax {
	public static void main(String args[]) {
		arguments = args;
		PAppletHax.main(P.concat(args, new String[] { "--bgcolor=#ff0000", "--window-color=#0000ff", "--location=0", Thread.currentThread().getStackTrace()[1].getClassName() }));
		// https://github.com/processing/processing4/blob/master/core/src/processing/awt/PSurfaceAWT.java
		// color arguments above don't seem to work, and location is wonky too
	}
	
	
	public float easeFactor = 6f;
	protected EasingFloat _easingX = new EasingFloat(0, 6f);
	protected EasingFloat _easingY = new EasingFloat(0, 6f);
	
	protected void config() {
		P.out("sketchWindowColor()", this.sketchWindowColor());
		// Config.setProperty( AppSettings.FPS, 90 );
//		setScreenSize();
//		 setFullscreen(true);
		// setFullscreenSpecificMonitor();
		// setFillAllScreens();
		 setUndecoratedWithScreenPosition(false);
	}

	protected void setScreenSize() {
		Config.setProperty( AppSettings.WIDTH, 540 );
		Config.setProperty( AppSettings.HEIGHT, 320 );
		Config.setProperty( AppSettings.APP_NAME, "Screen Size Tests" );
	}
	
	protected void setFullscreen(boolean alwaysOnTop) {
		Config.setProperty( AppSettings.FULLSCREEN, true );
		Config.setProperty( AppSettings.ALWAYS_ON_TOP, alwaysOnTop );
	}
	
	protected void setFullscreenSpecificMonitor() {
		Config.setProperty( AppSettings.FULLSCREEN, true );
		Config.setProperty( AppSettings.FULLSCREEN_SCREEN_NUMBER, 2 );
	}
	
	protected void setFillAllScreens() {
		Config.setProperty( AppSettings.SPAN_SCREENS, true );
	}
	
	protected void setUndecoratedWithScreenPosition(boolean alwaysOnTop) {
		Config.setProperty( AppSettings.FULLSCREEN, true );
		Config.setProperty( AppSettings.SCREEN_X, 50 );
		Config.setProperty( AppSettings.SCREEN_Y, 0 );
		Config.setProperty( AppSettings.WIDTH, 500 );
		Config.setProperty( AppSettings.HEIGHT, 300 );
		Config.setProperty( AppSettings.ALWAYS_ON_TOP, alwaysOnTop );
	}
	
	protected void drawApp() {
//		background(0, 255, 0);
		
		_easingX.setEaseFactor(easeFactor);
		_easingY.setEaseFactor(easeFactor);
		
		_easingX.setTarget(p.mouseX);
		_easingY.setTarget(p.mouseY);

		_easingX.update();
		_easingY.update();
		
		PG.setDrawCenter(p);
		p.fill(255);
		p.ellipse(_easingX.value(), _easingY.value(), 40, 40);
		
		if(Config.getBoolean(AppSettings.ALWAYS_ON_TOP, false) == true) {
			if(p.frameCount % 300 == 0) AppUtil.requestForegroundSafe();
		}
	}

}
