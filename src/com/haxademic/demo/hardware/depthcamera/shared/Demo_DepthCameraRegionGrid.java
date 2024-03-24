package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.DepthCameraRegionGrid;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;

public class Demo_DepthCameraRegionGrid
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DepthCameraRegionGrid kinectRegionGrid;
	
	public void config() {
		Config.setAppSize(1280, 720);
		Config.setProperty(AppSettings.DEPTH_CAM_RGB_ACTIVE, false);
	}
	
	protected void firstFrame() {
		int KINECT_MIN_DIST = 	300;
		int KINECT_MAX_DIST = 	1200;
		int KINECT_TOP = 		0;
		int KINECT_BOTTOM = 	DepthCameraSize.HEIGHT;
		int KINECT_PLAYER_GAP = 20;
		int KINECT_PIXEL_SKIP = 20;
		int PLAYER_MIN_PIXELS = 30;
		int COLS = 				2;
		int ROWS = 				1;
		int SIDE_MARGIN = 		100;
		
		// build input!
		RealSenseWrapper.setSmallStream();
		DepthCamera.instance(DepthCameraType.Realsense);
		kinectRegionGrid = new DepthCameraRegionGrid(COLS, ROWS, KINECT_MIN_DIST, KINECT_MAX_DIST, KINECT_PLAYER_GAP, KINECT_TOP, KINECT_BOTTOM, KINECT_PIXEL_SKIP, PLAYER_MIN_PIXELS, SIDE_MARGIN);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') kinectRegionGrid.toggleDebugOverhead();
	}
	
	protected void drawApp() {
		p.background(30);
		PG.setCenterScreen(p);
		PG.setDrawCenter(p);
		
		// update & draw grid
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		kinectRegionGrid.update(true);
		
		// draw debug to screen
		p.image(kinectRegionGrid.debugImage(), 0, 0);
		DebugView.setValue("kinectRegionGrid(0).controlX()", kinectRegionGrid.getRegion(0).controlX());
		
		// debug textures
//		if(depthCamera.getRgbImage() != null) DebugView.setTexture("depthCamera.getRgbImage", depthCamera.getRgbImage());
		if(depthCamera.getDepthImage() != null) DebugView.setTexture("depthCamera.getDepthImage", depthCamera.getDepthImage());
		if(kinectRegionGrid.debugImage() != null) DebugView.setTexture("kinectRegionGrid.debugImage", kinectRegionGrid.debugImage());
	}
	
}
