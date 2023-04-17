package com.haxademic.demo.app;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;

public class Demo_PAppletHax_DemoScreenshot
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
		Config.setProperty(AppSettings.RENDER_DEMO_SCREENSHOT, true);
		Config.setProperty(AppSettings.RENDER_DEMO_SCREENSHOT_FRAME, 100);
	}

	protected void drawApp() {
		background(0, 255, 0);
	}
}
