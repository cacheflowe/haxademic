package com.haxademic.demo.system;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

public class Demo_HourCheck
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void drawApp() {
		// update poller
		p.debugView.setValue("Hour", P.hour());
		p.debugView.setValue("Minute", P.minute());
		p.debugView.setValue("Second", P.second());
	}

}
