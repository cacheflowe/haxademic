package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.DepthCameraRegion;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.math.easing.EasingBoolean;
import com.haxademic.core.math.easing.EasingFloat;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class Demo_DepthCameraRegion
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// base components
	protected DepthCameraRegion region;
	protected PGraphics regionDebug;
	protected PGraphics joystickDebug;
	
	// ui
	protected String CAMERA_LEFT = "CAMERA_LEFT";
	protected String CAMERA_RIGHT = "CAMERA_RIGHT";
	protected String CAMERA_near = "CAMERA_near";
	protected String CAMERA_far = "CAMERA_far";
	protected String CAMERA_top = "CAMERA_top";
	protected String CAMERA_bottom = "CAMERA_bottom";
	protected String CAMERA_pixelSkip = "CAMERA_pixelSkip";
	protected String CAMERA_minPixels = "CAMERA_minPixels";
	protected String CAMERA_debug = "CAMERA_debug";
	
	// smoothed output
	protected EasingBoolean userActive = new EasingBoolean(false, 60);
	protected EasingFloat userX = new EasingFloat(0, 0.1f);
	protected EasingFloat userZ = new EasingFloat(0, 0.1f);

	
	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty( AppSettings.SHOW_UI, true );
	}
	
	protected void firstFrame() {
		DepthCamera.instance(DepthCameraType.Realsense);
		
		// add ui sliders to tweak at runtime
		UI.addTitle("DepthCamera Config");
		UI.addSlider(CAMERA_LEFT, 0, 0, DepthCameraSize.WIDTH, 1, false);
		UI.addSlider(CAMERA_RIGHT, DepthCameraSize.WIDTH, 0, DepthCameraSize.WIDTH, 1, false);
		UI.addSlider(CAMERA_near, 500, 0, 20000, 1, false);
		UI.addSlider(CAMERA_far, 1200, 0, 20000, 1, false);
		UI.addSlider(CAMERA_top, 0, 0, DepthCameraSize.HEIGHT, 1, false);
		UI.addSlider(CAMERA_bottom, DepthCameraSize.HEIGHT, 0, DepthCameraSize.HEIGHT, 1, false);
		UI.addSlider(CAMERA_pixelSkip, 20, 1, 30, 1, false);
		UI.addSlider(CAMERA_minPixels, 20, 1, 200, 1, false);
		UI.addToggle(CAMERA_debug, true, false);
		
		// build CAMERA region and debug buffer
		regionDebug = PG.newPG(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);
		joystickDebug = PG.newPG(200, 200);
		region = new DepthCameraRegion(
				UI.valueInt(CAMERA_LEFT), 
				UI.valueInt(CAMERA_RIGHT), 
				UI.valueInt(CAMERA_near), 
				UI.valueInt(CAMERA_far), 
				UI.valueInt(CAMERA_top),
				UI.valueInt(CAMERA_bottom), 
				UI.valueInt(CAMERA_pixelSkip), 
				UI.valueInt(CAMERA_minPixels), 
				0xff00ff00);
//		region = new DepthCameraRegion(0, DepthCameraSize.WIDTH, 0, 2000, 0, DepthCameraSize.HEIGHT, 10, 20, 0xffff0000);
	}
	
	public void keyPressed() {
		super.keyPressed();
	}
	
	protected void drawApp() {
		// context
		p.background(127);
		
		// set ui params
		region.left(UI.valueInt(CAMERA_LEFT));
		region.right(UI.valueInt(CAMERA_RIGHT));
		region.near(UI.valueInt(CAMERA_near));
		region.far(UI.valueInt(CAMERA_far));
		region.top(UI.valueInt(CAMERA_top));
		region.bottom(UI.valueInt(CAMERA_bottom));
		region.pixelSkip(UI.valueInt(CAMERA_pixelSkip));
		region.minPixels(UI.valueInt(CAMERA_minPixels));

		// debug info
//		DebugView.setValue("region.left", region.left());
//		DebugView.setValue("region.right", region.right());
//		DebugView.setValue("region.near", region.near());
//		DebugView.setValue("region.far", region.far());
//		DebugView.setValue("region.top", region.top());
//		DebugView.setValue("region.bottom", region.bottom());
//		DebugView.setValue("region.pixelSkip", region.pixelSkip());
		DebugView.setValue("region.isActive", region.isActive());
		DebugView.setValue("region.pixelCount", region.pixelCount());
		DebugView.setValue("region.minPixels", region.minPixels());
		DebugView.setValue("region.controlX", region.controlX());
		DebugView.setValue("region.controlY", region.controlY());
		DebugView.setValue("region.controlZ", region.controlZ());
		
		// update region and draw into debug buffer
		PGraphics debugPG = UI.valueToggle(CAMERA_debug) ? regionDebug : null;
		if(debugPG != null) {
			regionDebug.beginDraw();
			regionDebug.background(0);
			region.update(regionDebug);
			regionDebug.endDraw();
		} else {
			region.update();			
		}
		
		// update smoothed results
		userActive.target(region.isActive()).update();
		userX.setTarget(region.controlX()).update();
		userZ.setTarget(region.controlZ()).update();
		DebugView.setValue("userActive", userActive.value());
		DebugView.setValue("userX", userX.value());
		DebugView.setValue("userZ", userZ.value());
		
		// show debug buffer
		if(UI.valueToggle(CAMERA_debug)) {
			ImageUtil.cropFillCopyImage(regionDebug, p.g, false);
		}

		// draw debug to screen
		drawJoystickDebug();
		p.g.image(joystickDebug, p.width - joystickDebug.width, 0);
		
		// debug textures
		if(DepthCamera.instance().camera.getRgbImage() != null) DebugView.setTexture("depthCamera.getRgbImage", DepthCamera.instance().camera.getRgbImage());
		if(DepthCamera.instance().camera.getDepthImage() != null) DebugView.setTexture("depthCamera.getDepthImage", DepthCamera.instance().camera.getDepthImage());
		if(UI.valueToggle(CAMERA_debug)) DebugView.setTexture("DepthCameraRegion.debugImage", regionDebug);
		if(UI.valueToggle(CAMERA_debug)) DebugView.setTexture("joystickDebug", joystickDebug);
	}
	
	protected void drawJoystickDebug() {
		float debugSize = joystickDebug.width;
		joystickDebug.beginDraw();
		joystickDebug.background(0);
		joystickDebug.push();
		// draw debug bg
		PG.setDrawCorner(joystickDebug);
		PG.drawGrid(joystickDebug, 0xff111111, 0xff999999, 10, 10, 2, false);
		joystickDebug.rect(joystickDebug.width/2 - 2, 0, 4, pg.height);
		joystickDebug.rect(0, joystickDebug.height/2 - 2, pg.width, 4);
		// draw point
		PG.setDrawCenter(joystickDebug);
		joystickDebug.fill((userActive.value()) ? p.color(0,255,0) : p.color(255,0,0));
		joystickDebug.stroke(0);
		joystickDebug.strokeWeight(2);
		joystickDebug.ellipse(debugSize/2 + userX.value() * debugSize/2, debugSize/2 + userZ.value() * debugSize/2, 20, 20);
		joystickDebug.pop();
		joystickDebug.endDraw();
	}
	
}
