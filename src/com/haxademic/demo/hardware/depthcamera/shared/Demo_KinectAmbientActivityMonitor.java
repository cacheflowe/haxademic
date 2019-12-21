package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.depthcamera.KinectAmbientActivityMonitor;


public class Demo_KinectAmbientActivityMonitor 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public static final float PIXEL_SIZE = 6;
	public static final int KINECT_CLOSE = 500;
	public static final int KINECT_FAR = 10000;
		
	protected KinectAmbientActivityMonitor kinectMonitor;
	
	protected void config() {
//		Config.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
		Config.setProperty( AppSettings.KINECT_ACTIVE, true );
	}
	
	public void firstFrame() {
		kinectMonitor = new KinectAmbientActivityMonitor( PIXEL_SIZE, KINECT_CLOSE, KINECT_FAR );
	}
	
	public void drawApp() {
		p.background(0);
		p.image( p.depthCamera.getRgbImage(), 0, 0);

		float activityLevel = kinectMonitor.update(p.depthCamera, true );
		DebugView.setValue("KinectAmbientActivityMonitor", activityLevel / 1000);
	}
	
}
