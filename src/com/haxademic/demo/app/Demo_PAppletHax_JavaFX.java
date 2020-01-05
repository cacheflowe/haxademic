package com.haxademic.demo.app;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;

public class Demo_PAppletHax_JavaFX
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty( AppSettings.RENDERER, PRenderers.FX2D );
//		Config.setProperty( AppSettings.RENDERER, PRenderers.JAVA2D );
		Config.setProperty( AppSettings.WIDTH, 512 );
		Config.setProperty( AppSettings.HEIGHT, 256 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}
	
	protected void drawApp() {
		background(0);
	}

}
