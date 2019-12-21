package com.haxademic.app.sskiller;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;

public class ScreenSaverKiller
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 128 );
		Config.setProperty( AppSettings.HEIGHT, 128 );
	}

	public void firstFrame() {
	
	}

	public void drawApp() {
		p.background(127f + 127f * P.sin((float)p.frameCount * 0.1f));
	}
}
