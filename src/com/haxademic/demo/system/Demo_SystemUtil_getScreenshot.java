package com.haxademic.demo.system;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.system.SystemUtil;

public class Demo_SystemUtil_getScreenshot
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String _x = "_x";
	protected String _y = "_y";

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.SHOW_SLIDERS, true );
	}

	public void setupFirstFrame() {
		p.ui.addSlider(_x, 10, 0, p.displayWidth - p.width, 1, false);
		p.ui.addSlider(_y, 10, 1, p.displayHeight - p.height, 1, false);
	}

	public void drawApp() {
		background(0);
		p.image( SystemUtil.getScreenshot(p.ui.valueInt(_x), p.ui.valueInt(_y), p.width, p.height), 0, 0);
	}

}
