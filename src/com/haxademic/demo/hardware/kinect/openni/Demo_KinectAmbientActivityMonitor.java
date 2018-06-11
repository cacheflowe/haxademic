package com.haxademic.demo.hardware.kinect.openni;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.hardware.kinect.KinectAmbientActivityMonitor;


public class Demo_KinectAmbientActivityMonitor 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public static final float PIXEL_SIZE = 6;
	public static final int KINECT_CLOSE = 500;
	public static final int KINECT_FAR = 10000;
		
	protected KinectAmbientActivityMonitor kinectMonitor;
	
	public void setupFirstFrame() {
		kinectMonitor = new KinectAmbientActivityMonitor( PIXEL_SIZE, KINECT_CLOSE, KINECT_FAR );
	}

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, "true" );
		p.appConfig.setProperty( AppSettings.WIDTH, "640" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "480" );
	}
	
	public void drawApp() {
		p.background(0);
		p.image( p.kinectWrapper.getRgbImage(), 0, 0);

		float activityLevel = kinectMonitor.update(p.kinectWrapper, true );
		p.debugView.setValue("KinectAmbientActivityMonitor", activityLevel / 1000);
	}
	
}
