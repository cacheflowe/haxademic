package com.haxademic.demo.hardware.kinect.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.hardware.kinect.KinectAmbientActivityMonitor;


public class Demo_KinectAmbientActivityMonitor 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public static final float PIXEL_SIZE = 6;
	public static final int KINECT_CLOSE = 500;
	public static final int KINECT_FAR = 10000;
		
	protected KinectAmbientActivityMonitor kinectMonitor;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
	}
	
	public void setupFirstFrame() {
		kinectMonitor = new KinectAmbientActivityMonitor( PIXEL_SIZE, KINECT_CLOSE, KINECT_FAR );
	}
	
	public void drawApp() {
		p.background(0);
		p.image( p.kinectWrapper.getRgbImage(), 0, 0);

		float activityLevel = kinectMonitor.update(p.kinectWrapper, true );
		p.debugView.setValue("KinectAmbientActivityMonitor", activityLevel / 1000);
	}
	
}
