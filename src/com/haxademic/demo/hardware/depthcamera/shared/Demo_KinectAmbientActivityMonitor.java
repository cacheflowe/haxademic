package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.depthcamera.KinectAmbientActivityMonitor;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.math.easing.FloatBuffer;


public class Demo_KinectAmbientActivityMonitor 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public static final float PIXEL_SIZE = 10;
	public static final int KINECT_CLOSE = 500;
	public static final int KINECT_FAR = 10000;
		
	protected KinectAmbientActivityMonitor kinectMonitor;
	protected FloatBuffer activitySmoothed = new FloatBuffer(60);
	
	protected void firstFrame() {
		// init camera & monitoring object
		DepthCamera.instance(DepthCameraType.KinectV1);
		kinectMonitor = new KinectAmbientActivityMonitor( PIXEL_SIZE, KINECT_CLOSE, KINECT_FAR );
	}
	
	protected void drawApp() {
		// draw camera to screen
		p.background(0);
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		p.image(depthCamera.getRgbImage(), 0, 0);

		// calculate & display activity level
		float activityLevel = kinectMonitor.update(depthCamera, true );
		activityLevel /= 1000;
		
		// display activity
		DebugView.setValue("KinectAmbientActivityMonitor", activityLevel);
		p.fill(0, 255, 0);
		p.text("Activity level: " + (int) activityLevel, 20, p.height - 60);
		p.rect(10, p.height - 54, activityLevel, 10);

		// and smoothed activity
		activitySmoothed.update(activityLevel);
		p.fill(255, 255, 0);
		p.text("Activity level smoothed: " + (int) activitySmoothed.average(), 20, p.height - 30);
		p.rect(10, p.height - 24, activitySmoothed.average(), 10);
	}
	
}
