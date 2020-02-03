package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.KinectRegionGrid;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;

public class Demo_KinectRegionGrid
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectRegionGrid kinectRegionGrid;
	
	protected void firstFrame() {
		int KINECT_MIN_DIST = 	300;
		int KINECT_MAX_DIST = 	2000;
		int KINECT_TOP = 		0;
		int KINECT_BOTTOM = 	DepthCameraSize.HEIGHT;
		int KINECT_PLAYER_GAP = 30;
		int KINECT_PIXEL_SKIP = 20;
		int PLAYER_MIN_PIXELS = 30;
		int COLS = 				2;
		int ROWS = 				2;
		
		// build input!
		DepthCamera.instance(DepthCameraType.KinectV1);
		kinectRegionGrid = new KinectRegionGrid(COLS, ROWS, KINECT_MIN_DIST, KINECT_MAX_DIST, KINECT_PLAYER_GAP, KINECT_TOP, KINECT_BOTTOM, KINECT_PIXEL_SKIP, PLAYER_MIN_PIXELS);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') kinectRegionGrid.toggleDebugOverhead();
	}
	
	protected void drawApp() {
		p.background(0);
		
		// update & draw grid
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		kinectRegionGrid.update(true);
		p.image(kinectRegionGrid.debugImage(), 0, 0);
		
		// debug textures
		if(depthCamera.getRgbImage() != null) DebugView.setTexture("depthCamera.getRgbImage", depthCamera.getRgbImage());
		if(depthCamera.getDepthImage() != null) DebugView.setTexture("depthCamera.getDepthImage", depthCamera.getDepthImage());
		if(kinectRegionGrid.debugImage() != null) DebugView.setTexture("kinectRegionGrid.debugImage", kinectRegionGrid.debugImage());
	}
	
}
