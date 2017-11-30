package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.system.AppUtil;

public class Demo_AppUtil_writeRunScript
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected boolean DEBUG_MODE = true;

	protected void overridePropsFile() {
		appConfig.setProperty( AppSettings.FPS, 90 );
	}

	public void setup() {
		super.setup();
		AppUtil.writeRunScript("scripts/write-test-run.cmd");
	}

	public void drawApp() {
		p.background(0);
	}

}