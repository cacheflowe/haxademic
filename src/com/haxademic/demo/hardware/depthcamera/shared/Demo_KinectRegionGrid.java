package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
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
		int KINECT_MIN_DIST = 	Config.getInt( "kinect_min_mm", 500 );
		int KINECT_MAX_DIST = 	Config.getInt( "kinect_max_mm", 2000 );
		int KINECT_TOP = 		Config.getInt( "kinect_top_pixel", 0 );
		int KINECT_BOTTOM = 	Config.getInt( "kinect_bottom_pixel", DepthCameraSize.HEIGHT );
		int KINECT_PLAYER_GAP = Config.getInt( "kinect_player_gap", 0 );
		int COLS = 				Config.getInt( "num_players", 2 );
		int ROWS = 				2;
		int KINECT_PIXEL_SKIP = Config.getInt( "kinect_pixel_skip", 20 );
		int PLAYER_MIN_PIXELS = Config.getInt( "player_min_pixels", 10 );
		
		// build input!
		DepthCamera.instance(DepthCameraType.Realsense);
		kinectRegionGrid = new KinectRegionGrid(COLS, ROWS, KINECT_MIN_DIST, KINECT_MAX_DIST, KINECT_PLAYER_GAP, KINECT_TOP, KINECT_BOTTOM, KINECT_PIXEL_SKIP, PLAYER_MIN_PIXELS);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') kinectRegionGrid.toggleDebugOverhead();
	}
	
	protected void drawApp() {
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		p.background(0);
		
		// update & draw grid
		kinectRegionGrid.update(true);
		PG.setCenterScreen(p);
		PG.setDrawCenter(p);
		p.image(kinectRegionGrid.debugImage(), 0, 0);
		
		// debug textures
		if(depthCamera.getRgbImage() != null) DebugView.setTexture("depthCamera.getRgbImage", depthCamera.getRgbImage());
		if(depthCamera.getDepthImage() != null) DebugView.setTexture("depthCamera.getDepthImage", depthCamera.getDepthImage());
		if(kinectRegionGrid.debugImage() != null) DebugView.setTexture("kinectRegionGrid.debugImage", kinectRegionGrid.debugImage());
	}
	
}
