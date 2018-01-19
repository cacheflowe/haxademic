package com.haxademic.sketch.hardware.kinect_v2_windows;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;

public class Kinect2CameraTest
extends PAppletHax {

	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.SHOW_DEBUG, true );
	}

	public void setup() {
		super.setup();
	}

	public void drawApp() {
		p.background(0);
		p.debugView.setTexture(kinectWrapper.getRgbImage());
		p.debugView.setTexture(kinectWrapper.getIRImage());
		p.debugView.setTexture(kinectWrapper.getDepthImage());
		p.image(kinectWrapper.getDepthImage(), 0, 0);
//		p.image(kinect2.getDepthImage(), kinect2.depthWidth, 0);
//		p.image(kinect2.getIrImage(), 0, kinect2.depthHeight);
//		p.image(kinect2.getRegisteredImage(), kinect2.depthWidth, kinect2.depthHeight);
//		p.fill(255);
//		p.text("Framerate: " + (int)(frameRate), 10, 515);
	}

}
