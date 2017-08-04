package com.haxademic.app.sskiller;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

public class ScreenSaverKiller
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, "128" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "128" );
		p.appConfig.setProperty( AppSettings.INIT_ESS_AUDIO, false );
	}

	public void setup() {
		super.setup();	
	}

	public void drawApp() {
		p.background(127f + 127f * P.sin((float)p.frameCount * 0.1f));
	}
}
