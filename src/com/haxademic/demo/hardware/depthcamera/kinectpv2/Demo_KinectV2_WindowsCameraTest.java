package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

public class Demo_KinectV2_WindowsCameraTest
extends PAppletHax {

	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 900 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
	}

	public void drawApp() {
		p.background(0);
		p.debugView.setTexture("depthCamera.getRgbImage", depthCamera.getRgbImage());
		p.debugView.setTexture("depthCamera.getIRImage", depthCamera.getIRImage());
		p.debugView.setTexture("depthCamera.getDepthImage", depthCamera.getDepthImage());
		p.image(depthCamera.getDepthImage(), 0, 0);
		p.image(depthCamera.getIRImage(), depthCamera.getDepthImage().width, 0);
		p.image(depthCamera.getRgbImage(), 0, depthCamera.getDepthImage().height);
	}

}
