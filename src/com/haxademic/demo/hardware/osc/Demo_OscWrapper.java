package com.haxademic.demo.hardware.osc;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;

public class Demo_OscWrapper 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		 p.appConfig.setProperty( AppSettings.OSC_ACTIVE, true );
	}

	public void drawApp() {
		p.background(0);
		p.oscState.printButtons();
	}

}