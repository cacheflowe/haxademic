package com.haxademic.demo.app;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ColorsHax;

public class Demo_PAppletHax_Perf
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty( AppSettings.FPS, 90 );
		// Config.setProperty( AppSettings.WIDTH, 540 );
		// Config.setProperty( AppSettings.HEIGHT, 320 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, false );
	}
	
	protected void drawApp() {
		background(0);
		float spacing = 150;
		for (int i = 0; i < 50; i++) {
			// p.fill(ColorsHax.colorFromGroupAt(0, i));
			p.rect(-spacing + (p.frameCount * 3f % spacing) + i * spacing, 0, 30, p.height);
		}
	}

}
