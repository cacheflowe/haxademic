package com.haxademic.demo.app;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;

public class Demo_PAppletHax_Perf
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
//		p.appConfig.setProperty( AppSettings.FPS, 90 );
		p.appConfig.setProperty( AppSettings.WIDTH, 540 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 320 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}
	
	public void drawApp() {
		background(0);
	}

}
