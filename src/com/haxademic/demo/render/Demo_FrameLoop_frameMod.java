package com.haxademic.demo.render;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.StringBufferLog;
import com.haxademic.core.render.FrameLoop;

public class Demo_FrameLoop_frameMod
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }	
	
	protected StringBufferLog logOut = new StringBufferLog(30);

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 640 );
		Config.setProperty( AppSettings.LOOP_TICKS, 16 );
	}

	protected void firstFrame() {
	}

	protected void drawApp() {
		background(0);
		if(FrameLoop.frameModSeconds(2)) logOut.update("frameModSeconds(2) " + p.frameCount);
		if(FrameLoop.frameModSeconds(5)) logOut.update("frameModSeconds(5) " + p.frameCount);
		if(FrameLoop.frameModMinutes(0.5f)) logOut.update("frameModMinutes(0.5) " + p.frameCount);
		if(FrameLoop.frameModHours(1f/120f)) logOut.update("frameModHours(1/120) " + p.frameCount);
		logOut.printToScreen(p.g, 20, 20);
	}
}
