package com.haxademic.sketch.robbie;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.system.AppUtil;

public class Resizer 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
//	protected int changeSize;
	protected InputTrigger key1 = (new InputTrigger()).addKeyCodes(new char[]{'1'});
	protected InputTrigger key2 = (new InputTrigger()).addKeyCodes(new char[]{'2'});
	
	/////////////////////////////////
	// INIT
	/////////////////////////////////
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 600 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.PG_WIDTH, 600 );
		p.appConfig.setProperty( AppSettings.PG_HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_NONE );
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false );
		p.appConfig.setProperty( AppSettings.APP_NAME, "Basic" );
		p.appConfig.setProperty( AppSettings.APP_ICON, "images/app-icon.png" );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, false);
	}
	
	public void setupFirstFrame() {
		p.background(0);
		p.noStroke();
	}

	public void drawApp() {
		p.background(100);
		if(key1.on()) {
			if (p.width != 300) resizeApp(300);
		} else {
			if (p.width != 600) resizeApp(600);
		}
		
	}
	
	public void resizeApp(int size) {
//		p.frame.setSize(size, size);
		AppUtil.setSize(p, size, size);
	}
	
	
//	public static void setSize(PApplet p, int w, int h) {
//		p.getSurface().setSize(w, h);
//	}
}