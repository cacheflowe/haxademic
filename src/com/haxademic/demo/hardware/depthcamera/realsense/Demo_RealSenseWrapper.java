package com.haxademic.demo.hardware.depthcamera.realsense;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;

public class Demo_RealSenseWrapper
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected RealSenseWrapper realSenseWrapper;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 900 );
	}


	protected void setupFirstFrame() {
		realSenseWrapper = new RealSenseWrapper(p, true, true);
	}

	public void drawApp() {
		realSenseWrapper.update();
		p.background(0);
		pg.beginDraw();
		pg.image(realSenseWrapper.getRgbImage(), 0, 0);
		pg.image(realSenseWrapper.getDepthImage(), 0, realSenseWrapper.getRgbImage().height);
		pg.endDraw();
		p.image(pg, 0, 0);
	}
	
}
