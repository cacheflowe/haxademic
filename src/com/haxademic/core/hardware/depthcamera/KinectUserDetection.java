package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.app.P;

public class KinectUserDetection {

	protected KinectRegionGrid kinectRegionGrid;

	public KinectUserDetection(int cols, int rows) {
		// build detection grid
		int KINECT_MIN_DIST = 	P.p.appConfig.getInt( "kinect_min_mm", 500 );
		int KINECT_MAX_DIST = 	P.p.appConfig.getInt( "kinect_max_mm", 1000 );
		int KINECT_TOP = 		P.p.appConfig.getInt( "kinect_top_pixel", 0 );
		int KINECT_BOTTOM = 	P.p.appConfig.getInt( "kinect_bottom_pixel", DepthCameraSize.HEIGHT );
		int KINECT_PLAYER_GAP = P.p.appConfig.getInt( "kinect_player_gap", 0 );
		int KINECT_PIXEL_SKIP = P.p.appConfig.getInt( "kinect_pixel_skip", 20 );
		int PLAYER_MIN_PIXELS = P.p.appConfig.getInt( "player_min_pixels", 10 );
		kinectRegionGrid = new KinectRegionGrid(cols, rows, KINECT_MIN_DIST, KINECT_MAX_DIST, KINECT_PLAYER_GAP, KINECT_TOP, KINECT_BOTTOM, KINECT_PIXEL_SKIP, PLAYER_MIN_PIXELS);
	}
	
	public void update() {
		boolean debugging = P.p.debugView.active();
		kinectRegionGrid.update(debugging);
		if(kinectRegionGrid.debugImage() != null) {
			P.p.debugView.setTexture("kinectRegionGrid.debugImage", kinectRegionGrid.debugImage());
		}

		// draw active indicator
//		P.p.debugView.setValue("KINECT USER ACTIVE", kinectRegionGrid.getRegion(1).isActive());
//		int activeColor = kinectRegionGrid.getRegion(1).isActive() ? p.color(0,255,0) : p.color(255,0,0);
//		pg.fill(activeColor);
//		pg.rect(0,0, 100, 100);
	}
	
	public void toggleDebug() {
		kinectRegionGrid.toggleDebugOverhead();
	}

}