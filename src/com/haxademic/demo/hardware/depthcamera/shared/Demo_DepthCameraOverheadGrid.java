package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.depthcamera.DepthCameraOverheadGrid;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.ui.UI;

public class Demo_DepthCameraOverheadGrid
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected DepthCameraOverheadGrid overheadRegionGrid;

	protected String DEBUG = "DEBUG_MODE";
	
	public void config() {
		Config.setAppSize(1280, 720);
		Config.setProperty(AppSettings.SHOW_DEBUG, true);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame() {
		buildUI();
		buildOverheadGrid();
	}
	
	protected void buildUI() {
		UI.addTitle("DepthCameraOverheadGrid");
		UI.addToggle(DEBUG, false, false);
	}
	
	protected void buildOverheadGrid() {
		int DEPTH_MIN_DIST = 	300;
		int DEPTH_MAX_DIST = 	700;
		float DEPTH_LEFT = 		0.1f;
		float DEPTH_RIGHT = 	0.1f;
		float DEPTH_TOP = 		0.1f;
		float DEPTH_BOTTOM = 	0.4f;
		float DEPTH_PLAYER_GAP = 0.1f;
		int DEPTH_PIXEL_SKIP = 	7;
		int ACTIVE_MIN_PIXELS = 30;
		int COLS = 				2;
		int ROWS = 				1;
		
		// init camera input!
		RealSenseWrapper.CAMERA_W = 640;
		RealSenseWrapper.CAMERA_H = 480;
		RealSenseWrapper.METERS_FAR_THRESH = 2;
		DepthCamera.instance(DepthCameraType.Realsense);
		overheadRegionGrid = new DepthCameraOverheadGrid(COLS, ROWS, DEPTH_MIN_DIST, DEPTH_MAX_DIST, DEPTH_PLAYER_GAP, DEPTH_LEFT, DEPTH_RIGHT, DEPTH_TOP, DEPTH_BOTTOM, DEPTH_PIXEL_SKIP, ACTIVE_MIN_PIXELS);
		DebugView.setTexture("DepthCameraOverheadGrid silhouette", overheadRegionGrid.debugImage());
	}
	
	public void keyPressed() {
		super.keyPressed();
	}
	
	protected void drawApp() {
		p.background(100);
		PG.setDrawCorner(p.g);
		
		// update & draw grid tracking
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		overheadRegionGrid.update(UI.valueToggle(DEBUG));
		if(overheadRegionGrid.debugImage() != null) {
			p.image(overheadRegionGrid.debugImage(), 0, 0, overheadRegionGrid.debugImage().width * 4, overheadRegionGrid.debugImage().height * 4);
		}
		
		// show controls results in DebugView
		DebugView.setValue("DepthRegionGrid(0).controlX()", overheadRegionGrid.getRegion(0).controlX());
		DebugView.setValue("DepthRegionGrid(0).controlZ()", overheadRegionGrid.getRegion(0).controlZ());
		DebugView.setValue("DepthRegionGrid(1).controlX()", overheadRegionGrid.getRegion(1).controlX());
		DebugView.setValue("DepthRegionGrid(1).controlZ()", overheadRegionGrid.getRegion(1).controlZ());
		
		// debug textures
		if(depthCamera.getRgbImage() != null) DebugView.setTexture("depthCamera.getRgbImage", depthCamera.getRgbImage());
		if(depthCamera.getDepthImage() != null) DebugView.setTexture("depthCamera.getDepthImage", depthCamera.getDepthImage());
		if(overheadRegionGrid.debugImage() != null) DebugView.setTexture("DepthRegionGrid.debugImage", overheadRegionGrid.debugImage());
		
		// draw objects to indicate values
		p.push();
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
		float playerAmp = 100;
		float playerSize = 30;
		p.fill(255, 255, 0);
		p.ellipse(playerAmp * overheadRegionGrid.getRegion(0).controlX(), playerAmp * overheadRegionGrid.getRegion(0).controlZ(), playerSize, playerSize);
		p.fill(0, 255, 255);
		p.ellipse(playerAmp * overheadRegionGrid.getRegion(1).controlX(), playerAmp * overheadRegionGrid.getRegion(1).controlZ(), playerSize, playerSize);
		p.pop();
	}
	
}
