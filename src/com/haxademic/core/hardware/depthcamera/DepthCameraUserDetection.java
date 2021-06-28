package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;

public class DepthCameraUserDetection {

	protected DepthCameraRegionGrid kinectRegionGrid;

	public DepthCameraUserDetection(int cols, int rows) {
		// build detection grid
		int KINECT_MIN_DIST = 	Config.getInt( "kinect_min_mm", 500 );
		int KINECT_MAX_DIST = 	Config.getInt( "kinect_max_mm", 1000 );
		int KINECT_TOP = 		Config.getInt( "kinect_top_pixel", 0 );
		int KINECT_BOTTOM = 	Config.getInt( "kinect_bottom_pixel", DepthCameraSize.HEIGHT );
		int KINECT_PLAYER_GAP = Config.getInt( "kinect_player_gap", 0 );
		int KINECT_PIXEL_SKIP = Config.getInt( "kinect_pixel_skip", 20 );
		int PLAYER_MIN_PIXELS = Config.getInt( "player_min_pixels", 10 );
		kinectRegionGrid = new DepthCameraRegionGrid(cols, rows, KINECT_MIN_DIST, KINECT_MAX_DIST, KINECT_PLAYER_GAP, KINECT_TOP, KINECT_BOTTOM, KINECT_PIXEL_SKIP, PLAYER_MIN_PIXELS);
	}
	
	public void update() {
		boolean debugging = DebugView.active();
		kinectRegionGrid.update(debugging);
		if(kinectRegionGrid.debugImage() != null) {
			DebugView.setTexture("kinectRegionGrid.debugImage", kinectRegionGrid.debugImage());
		}

		// draw active indicator
//		DebugView.setValue("KINECT USER ACTIVE", kinectRegionGrid.getRegion(1).isActive());
//		int activeColor = kinectRegionGrid.getRegion(1).isActive() ? p.color(0,255,0) : p.color(255,0,0);
//		pg.fill(activeColor);
//		pg.rect(0,0, 100, 100);
	}
	
	public void toggleDebug() {
		kinectRegionGrid.toggleDebugOverhead();
	}

}