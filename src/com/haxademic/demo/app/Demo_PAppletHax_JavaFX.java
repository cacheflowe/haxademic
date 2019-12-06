package com.haxademic.demo.app;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.data.constants.PRenderers;

public class Demo_PAppletHax_JavaFX
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERER, PRenderers.FX2D );
//		p.appConfig.setProperty( AppSettings.RENDERER, PRenderers.JAVA2D );
		p.appConfig.setProperty( AppSettings.WIDTH, 512 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 256 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}
	
	public void drawApp() {
		background(0);
	}

}
