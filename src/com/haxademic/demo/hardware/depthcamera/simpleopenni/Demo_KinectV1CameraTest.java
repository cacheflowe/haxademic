package com.haxademic.demo.hardware.depthcamera.simpleopenni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

public class Demo_KinectV1CameraTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 480 );
		p.appConfig.setProperty(AppSettings.KINECT_ACTIVE, true );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}
	
	public void drawApp() {
		p.image(p.depthCamera.getRgbImage(), 0, 0);
		p.image(p.depthCamera.getDepthImage(), 640, 0);
		if(p.depthCamera.getRgbImage() != null) p.debugView.setTexture("depthCamera.getRgbImage", p.depthCamera.getRgbImage());
		if(p.depthCamera.getDepthImage() != null) p.debugView.setTexture("depthCamera.getDepthImage", p.depthCamera.getDepthImage());
	}
	
}
