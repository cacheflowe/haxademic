package com.haxademic.demo.hardware.depthcamera.realsense;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;

public class Demo_RealSenseWrapper
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected RealSenseWrapper realSenseWrapper;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 960 );
	}


	protected void firstFrame() {
		realSenseWrapper = new RealSenseWrapper(p, false, true);
	}

	protected void drawApp() {
		p.background(0);
		realSenseWrapper.update();
		p.image(realSenseWrapper.getRgbImage(), 0, 0);
		p.image(realSenseWrapper.getDepthImage(), 0, realSenseWrapper.getRgbImage().height);
	}
	
}
