package com.haxademic.demo.draw.context;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;

public class Demo_PG_pixelFlushPattern
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setAppSize(1280, 720);
		Config.setProperty(AppSettings.FULLSCREEN, true);
	}
		
	protected void drawApp() {
		if(p.frameCount % 2 == 0) {
			PG.pixelFlushPattern(p.g, false);
		}
	}
}
