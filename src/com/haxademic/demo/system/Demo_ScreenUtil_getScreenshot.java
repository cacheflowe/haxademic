package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.system.ScreenshotUtil;
import com.haxademic.core.ui.UI;

import processing.core.PImage;

public class Demo_ScreenUtil_getScreenshot
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String _x = "_x";
	protected String _y = "_y";
	
	protected PImage img;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 600 );
		Config.setProperty( AppSettings.SHOW_UI, true );
	}

	public void firstFrame() {
		UI.addSlider(_x, 10, 0, p.displayWidth - p.width, 1, false);
		UI.addSlider(_y, 10, 1, p.displayHeight - p.height, 1, false);
	}

	public void drawApp() {
		background(0);
//		 p.image( ScreenUtil.getScreenshotMainMonitor(UI.valueInt(_x), UI.valueInt(_y), p.width, p.height), 0, 0);	// deprecated version
		p.image( ScreenshotUtil.getScreenShotAsPImage(UI.valueInt(_x), UI.valueInt(_y), p.width, p.height), 0, 0);
//		p.image( ScreenUtil.getScreenShotAllMonitors(0, 0, 0.5f), 0, 0 );
	}

}
