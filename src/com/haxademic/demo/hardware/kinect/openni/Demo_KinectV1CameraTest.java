package com.haxademic.demo.hardware.kinect.openni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;

public class Demo_KinectV1CameraTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.WIDTH, 1280 );
		p.appConfig.setProperty(AppSettings.HEIGHT, 480 );
		p.appConfig.setProperty(AppSettings.KINECT_ACTIVE, true );
		p.appConfig.setProperty(AppSettings.SHOW_DEBUG, true );
	}
	
	public void setupFirstFrame() {
		
	}
	
	public void drawApp() {
		p.image(p.kinectWrapper.getRgbImage(), 0, 0);
		p.image(p.kinectWrapper.getDepthImage(), 640, 0);
		if(p.kinectWrapper.getRgbImage() != null) p.debugView.setTexture(p.kinectWrapper.getRgbImage());
		if(p.kinectWrapper.getDepthImage() != null) p.debugView.setTexture(p.kinectWrapper.getDepthImage());
	}
	
}
