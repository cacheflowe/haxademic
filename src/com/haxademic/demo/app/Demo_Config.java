package com.haxademic.demo.app;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.file.FileUtil;

public class Demo_Config
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.loadPropertiesFile(FileUtil.getPath("properties/haxvisual.properties"));
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.SHOW_FPS_IN_TITLE, true );
		Config.setProperty( AppSettings.PG_32_BIT, true );
	}
	
	protected void firstFrame() {
		P.out("testArg =", Config.getArgValue("testArg"));	// need to pass in command line arg of `testArg=HELLO`
	}
	
	protected void drawApp() {
		background(0);
	}

}
