package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.KinectRegionGrid;

public class Demo_KinectRegionGrid
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectRegionGrid kinectRegionGrid;
	
	protected void overridePropsFile() {
//		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
	}
	
	public void setupFirstFrame() {
		int KINECT_MIN_DIST = 	p.appConfig.getInt( "kinect_min_mm", 500 );
		int KINECT_MAX_DIST = 	p.appConfig.getInt( "kinect_max_mm", 2000 );
		int KINECT_TOP = 		p.appConfig.getInt( "kinect_top_pixel", 0 );
		int KINECT_BOTTOM = 	p.appConfig.getInt( "kinect_bottom_pixel", DepthCameraSize.HEIGHT );
		int KINECT_PLAYER_GAP = p.appConfig.getInt( "kinect_player_gap", 0 );
		int COLS = 				p.appConfig.getInt( "num_players", 2 );
		int ROWS = 				2;
		int KINECT_PIXEL_SKIP = p.appConfig.getInt( "kinect_pixel_skip", 20 );
		int PLAYER_MIN_PIXELS = p.appConfig.getInt( "player_min_pixels", 10 );
		
		// build input!
		kinectRegionGrid = new KinectRegionGrid(COLS, ROWS, KINECT_MIN_DIST, KINECT_MAX_DIST, KINECT_PLAYER_GAP, KINECT_TOP, KINECT_BOTTOM, KINECT_PIXEL_SKIP, PLAYER_MIN_PIXELS);
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') kinectRegionGrid.toggleDebugOverhead();
	}
	
	public void drawApp() {
		// context
		p.background(0);
		
		// update & draw grid
		kinectRegionGrid.update(true);
		PG.setCenterScreen(p);
		PG.setDrawCenter(p);
		p.image(kinectRegionGrid.debugImage(), 0, 0);
		
		// debug textures
		if(p.depthCamera.getRgbImage() != null) p.debugView.setTexture("depthCamera.getRgbImage", p.depthCamera.getRgbImage());
		if(p.depthCamera.getDepthImage() != null) p.debugView.setTexture("depthCamera.getDepthImage", p.depthCamera.getDepthImage());
		if(kinectRegionGrid.debugImage() != null) p.debugView.setTexture("kinectRegionGrid.debugImage", kinectRegionGrid.debugImage());
	}
	
}
