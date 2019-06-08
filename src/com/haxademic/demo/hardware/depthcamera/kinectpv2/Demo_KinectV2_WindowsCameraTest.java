package com.haxademic.demo.hardware.depthcamera.kinectpv2;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;

public class Demo_KinectV2_WindowsCameraTest
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1200 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 900 );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
	}

	public void drawApp() {
		p.background(0);
		p.debugView.setTexture(kinectWrapper.getRgbImage());
		p.debugView.setTexture(kinectWrapper.getIRImage());
		p.debugView.setTexture(kinectWrapper.getDepthImage());
		p.image(kinectWrapper.getDepthImage(), 0, 0);
		p.image(kinectWrapper.getIRImage(), kinectWrapper.getDepthImage().width, 0);
		p.image(kinectWrapper.getRgbImage(), 0, kinectWrapper.getDepthImage().height);
	}

}
