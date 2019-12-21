package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.KinectRegion;
import com.haxademic.core.math.easing.EasingBoolean;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_KinectRegion
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// base components
	protected KinectRegion region;
	protected PGraphics regionDebug;
	
	// ui
	protected String KINECT_left = "KINECT_left";
	protected String KINECT_right = "KINECT_right";
	protected String KINECT_near = "KINECT_near";
	protected String KINECT_far = "KINECT_far";
	protected String KINECT_top = "KINECT_top";
	protected String KINECT_bottom = "KINECT_bottom";
	protected String KINECT_pixelSkip = "KINECT_pixelSkip";
	protected String KINECT_minPixels = "KINECT_minPixels";
	
	// smoothed output
	protected EasingBoolean userActive = new EasingBoolean(false, 180);
	protected EasingFloat userX = new EasingFloat(0, 0.1f);
	protected EasingFloat userZ = new EasingFloat(0, 0.1f);

	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1024 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 512 );
		p.appConfig.setProperty( AppSettings.SHOW_UI, true );
		p.appConfig.setProperty( AppSettings.KINECT_V2_WIN_ACTIVE, true );
//		p.appConfig.setProperty( AppSettings.KINECT_ACTIVE, true );
	}
	
	public void addKeyCommandInfo() {
		// disable key command window - it's in the way
	}
	
	public void setupFirstFrame() {
		// build kinect region and debug buffer
		regionDebug = PG.newPG(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);
		region = new KinectRegion(0, DepthCameraSize.WIDTH, 0, 2000, 0, DepthCameraSize.HEIGHT, 10, 20, 0xffff0000);
		
		// add ui sliders to tweak at runtime
		UI.addSlider(KINECT_left, 0, 0, DepthCameraSize.WIDTH, 1, false);
		UI.addSlider(KINECT_right, DepthCameraSize.WIDTH, 0, DepthCameraSize.WIDTH, 1, false);
		UI.addSlider(KINECT_near, 500, 0, 1, 1, false);
		UI.addSlider(KINECT_far, 2000, 0, 20000, 1, false);
		UI.addSlider(KINECT_top, 0, 0, DepthCameraSize.HEIGHT, 1, false);
		UI.addSlider(KINECT_bottom, DepthCameraSize.HEIGHT, 0, DepthCameraSize.HEIGHT, 1, false);
		UI.addSlider(KINECT_pixelSkip, 10, 1, 30, 1, false);
		UI.addSlider(KINECT_minPixels, 20, 1, 200, 1, false);
	}
	
	public void keyPressed() {
		super.keyPressed();
	}
	
	public void drawApp() {
		// context
		p.background(127);
		
		// set ui params
		region.left(UI.valueInt(KINECT_left));
		region.right(UI.valueInt(KINECT_right));
		region.near(UI.valueInt(KINECT_near));
		region.far(UI.valueInt(KINECT_far));
		region.top(UI.valueInt(KINECT_top));
		region.bottom(UI.valueInt(KINECT_bottom));
		region.pixelSkip(UI.valueInt(KINECT_pixelSkip));
		region.minPixels(UI.valueInt(KINECT_minPixels));
		
		// update region and draw into debug buffer
		regionDebug.beginDraw();
		regionDebug.background(0);
		region.update(regionDebug);
		regionDebug.endDraw();
		
		// show debug buffer
		p.image(regionDebug, p.width - regionDebug.width, 0);
		ImageUtil.cropFillCopyImage(regionDebug, p.g, false);

		// update smoothed results
		userActive.target(region.isActive()).update();
		userX.setTarget(region.controlX()).update();
		userZ.setTarget(region.controlZ()).update();
		
		p.debugView.setValue("userActive", userActive.value());
		p.debugView.setValue("userX", userX.value());
		p.debugView.setValue("userZ", userZ.value());
		
		p.pushMatrix();
		PG.setDrawCenter(p);
		p.translate(p.width - regionDebug.width/2, regionDebug.height/2); // move to center of debug image
		p.fill((userActive.value()) ? p.color(0,255,0) : p.color(255,0,0));
		p.ellipse(userX.value() * regionDebug.width/2, userZ.value() * regionDebug.height/2, 20, 20);
		PG.setDrawCorner(p);
		p.popMatrix();
		
		// debug info
		p.debugView.setValue("region.controlX", region.controlX());
		p.debugView.setValue("region.controlY", region.controlY());
		p.debugView.setValue("region.controlZ", region.controlZ());
		p.debugView.setValue("region.isActive", region.isActive());
		p.debugView.setValue("region.left", region.left());
		p.debugView.setValue("region.right", region.right());
		p.debugView.setValue("region.near", region.near());
		p.debugView.setValue("region.far", region.far());
		p.debugView.setValue("region.top", region.top());
		p.debugView.setValue("region.bottom", region.bottom());
		p.debugView.setValue("region.pixelSkip", region.pixelSkip());
		p.debugView.setValue("region.pixelCount", region.pixelCount());
		p.debugView.setValue("region.minPixels", region.minPixels());
		
		// debug textures
//		if(p.depthCamera.getRgbImage() != null) p.debugView.setTexture("depthCamera.getRgbImage", p.depthCamera.getRgbImage());
//		if(p.depthCamera.getDepthImage() != null) p.debugView.setTexture("depthCamera.getDepthImage", p.depthCamera.getDepthImage());
		if(regionDebug != null) p.debugView.setTexture("kinectRegionGrid.debugImage", regionDebug);
	}
	
}
