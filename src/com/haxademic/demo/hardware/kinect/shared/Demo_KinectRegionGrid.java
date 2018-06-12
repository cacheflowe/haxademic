package com.haxademic.demo.hardware.kinect.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.hardware.kinect.KinectRegionGrid;
import com.haxademic.core.hardware.kinect.KinectSize;

public class Demo_KinectRegionGrid
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected KinectRegionGrid kinectRegionGrid;
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
	}
	
	public void setupFirstFrame() {
		int KINECT_MIN_DIST = 	p.appConfig.getInt( "kinect_min_mm", 1500 );
		int KINECT_MAX_DIST = 	p.appConfig.getInt( "kinect_max_mm", 2000 );
		int KINECT_TOP = 		p.appConfig.getInt( "kinect_top_pixel", 0 );
		int KINECT_BOTTOM = 	p.appConfig.getInt( "kinect_bottom_pixel", KinectSize.HEIGHT );
		int KINECT_PLAYER_GAP = p.appConfig.getInt( "kinect_player_gap", 0 );
		int NUM_PLAYERS = 		p.appConfig.getInt( "num_players", 2 );
		int KINECT_PIXEL_SKIP = p.appConfig.getInt( "kinect_pixel_skip", 20 );
		int PLAYER_MIN_PIXELS = p.appConfig.getInt( "player_min_pixels", 10 );
		
		// build input!
		kinectRegionGrid = new KinectRegionGrid(NUM_PLAYERS, 3, KINECT_MIN_DIST, KINECT_MAX_DIST, KINECT_PLAYER_GAP, KINECT_TOP, KINECT_BOTTOM, KINECT_PIXEL_SKIP, PLAYER_MIN_PIXELS);
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
		DrawUtil.setCenterScreen(p);
		DrawUtil.setDrawCenter(p);
		p.image(kinectRegionGrid.debugImage(), 0, 0);
		
		// debug textures
		if(p.kinectWrapper.getRgbImage() != null) p.debugView.setTexture(p.kinectWrapper.getRgbImage());
		if(p.kinectWrapper.getDepthImage() != null) p.debugView.setTexture(p.kinectWrapper.getDepthImage());
		if(kinectRegionGrid.debugImage() != null) p.debugView.setTexture(kinectRegionGrid.debugImage());
	}
	
}
