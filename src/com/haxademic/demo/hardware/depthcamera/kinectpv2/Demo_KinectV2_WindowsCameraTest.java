package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;

public class Demo_KinectV2_WindowsCameraTest
extends PAppletHax {

	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1200 );
		Config.setProperty( AppSettings.HEIGHT, 900 );
		Config.setProperty( AppSettings.SHOW_DEBUG, true );
		Config.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		Config.setProperty( AppSettings.KINECT_ACTIVE, true );
	}

	public void drawApp() {
		p.background(0);
		DebugView.setTexture("depthCamera.getRgbImage", depthCamera.getRgbImage());
		DebugView.setTexture("depthCamera.getIRImage", depthCamera.getIRImage());
		DebugView.setTexture("depthCamera.getDepthImage", depthCamera.getDepthImage());
		p.image(depthCamera.getDepthImage(), 0, 0);
		p.image(depthCamera.getIRImage(), depthCamera.getDepthImage().width, 0);
		p.image(depthCamera.getRgbImage(), 0, depthCamera.getDepthImage().height);
	}

}
