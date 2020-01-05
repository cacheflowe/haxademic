package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.depthcamera.KinectAmbientActivityMonitor;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;


public class Demo_KinectAmbientActivityMonitor 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public static final float PIXEL_SIZE = 6;
	public static final int KINECT_CLOSE = 500;
	public static final int KINECT_FAR = 10000;
		
	protected KinectAmbientActivityMonitor kinectMonitor;
	
	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.KinectV1);
		kinectMonitor = new KinectAmbientActivityMonitor( PIXEL_SIZE, KINECT_CLOSE, KINECT_FAR );
	}
	
	protected void drawApp() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;

		p.background(0);
		p.image(depthCamera.getRgbImage(), 0, 0);

		float activityLevel = kinectMonitor.update(depthCamera, true );
		DebugView.setValue("KinectAmbientActivityMonitor", activityLevel / 1000);
	}
	
}
