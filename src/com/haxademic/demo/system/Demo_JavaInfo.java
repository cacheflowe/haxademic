package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.system.JavaInfo;

public class Demo_JavaInfo
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void firstFrame() {
		JavaInfo.printDebug();
	}
	
	public void drawApp() {
		background(0);
	}

}
