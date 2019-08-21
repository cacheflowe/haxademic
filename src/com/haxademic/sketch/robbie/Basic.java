package com.haxademic.sketch.robbie;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

public class Basic 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
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
	}
	
}