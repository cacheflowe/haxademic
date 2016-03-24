package com.haxademic.app.sskiller;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

@SuppressWarnings("serial")
public class ScreenSaverKiller
extends PAppletHax {
	
	protected void overridePropsFile() {
		_appConfig.setProperty( "width", "128" );
		_appConfig.setProperty( "height", "128" );
	}

	public void setup() {
		super.setup();	
	}

	public void drawApp() {
		p.background(127f + 127f * P.sin((float)p.frameCount * 0.1f));
	}
}
