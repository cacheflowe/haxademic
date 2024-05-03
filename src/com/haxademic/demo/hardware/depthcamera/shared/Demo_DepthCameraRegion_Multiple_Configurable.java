package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.mapping.PGraphicsKeystone;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.DepthCameraRegion;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.hardware.joystick.BaseJoystick;
import com.haxademic.core.hardware.joystick.IJoystickControl.IJoystickActiveDelegate;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_DepthCameraRegion_Multiple_Configurable
extends PAppletHax
implements IAppStoreListener, IJoystickActiveDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// regions
	protected DepthCameraRegion[] regions;
	protected DepthCameraRegion activeRegion;

	// debug
	protected PGraphics regionDebug;		  // updated by the `region` object
	protected PGraphics regionFlatDebug;	// updated by the `region` object
	
	// UI
	protected String REGION_INDEX = "REGION_INDEX";
	protected String CAMERA_DEBUG_3D = "CAMERA_DEBUG_3D";

	// region IDs
	protected String CLICK_1 = "CLICK_1";
	protected String CLICK_2 = "CLICK_2";

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.DEPTH_CAM_RGB_ACTIVE, false);
	}
	
	protected void firstFrame() {
		initCameras();
		buildRegions();
		initDebugBuffers();
		initKeystones();
		P.store.addListener(this);
	}

	protected void buildRegions() {
		// build multiple regions
		regions = new DepthCameraRegion[] {
			(new DepthCameraRegion()).id(CLICK_1).clickMode(true),
			(new DepthCameraRegion()).id(CLICK_2).clickMode(true),
			(new DepthCameraRegion()).id("Debug"), // entire camera FOV for visual debugging
		};
		activeRegion = regions[0];

		// build shared UI for region settings
		DepthCameraRegion.buildGenericUI();

		// set active callback delegate for all regions
		for (int i = 0; i < regions.length; i++) {
			regions[i].setActiveDelegate(this);
			regions[i].updatePropsFromGenericUI(); // set default UI values
			regions[i].loadConfig(FileUtil.haxademicDataPath() + "text/json/depth-camera-regions/region-"+P.nf(i, 3)+".json");
		}

		// load first region into UI - this immediately starts updating the region
		setRegionToUI(0);
	}

	protected void setRegionToUI(int index) {
		// deactivate prior region
		if(activeRegion != null) activeRegion.isEditing(false); 
		// activate new region
		activeRegion = regions[index];
		activeRegion.isEditing(true);
		DepthCameraRegion.setGenericUiFromRegion(activeRegion);
	}

	protected void initCameras() {
		// RealSenseWrapper.setTinyStream();
		RealSenseWrapper.setSmallStream();
		DepthCamera.instance(DepthCameraType.Realsense);
	}

	protected void initDebugBuffers() {
		// add ui sliders to tweak at runtime
		UI.addTitle("DepthCamera Debug");
		UI.addSlider(REGION_INDEX, 0, 0, regions.length - 1, 1, false);
		UI.addToggle(CAMERA_DEBUG_3D, true, false);

		// build CAMERA region and debug buffer
		regionDebug = PG.newPG(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);
		regionFlatDebug = PG.newPG(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);
	}
	
	protected void updateDepthRegions() {
		// apply UI controls
		activeRegion.updatePropsFromGenericUI();

		// update depth buffer
		PGraphics debugPG = UI.valueToggle(CAMERA_DEBUG_3D) ? regionDebug : null;
		// update depth data
		// draw old 3d debug view at the same time if toggled 
		if(debugPG != null) {
			regionDebug.beginDraw();
			regionDebug.background(0);
			for (int i = 0; i < regions.length; i++) regions[i].update(regionDebug);
			regionDebug.endDraw();
		} else {
			for (int i = 0; i < regions.length; i++) regions[i].update();
		}
	}
		
	protected void addDebugTextures() {
		if(DepthCamera.instance().camera.getRgbImage() != null) DebugView.setTexture("depthCamera.getRgbImage", DepthCamera.instance().camera.getRgbImage());
		if(DepthCamera.instance().camera.getDepthImage() != null) DebugView.setTexture("depthCamera.getDepthImage", DepthCamera.instance().camera.getDepthImage());
		if(UI.valueToggle(CAMERA_DEBUG_3D)) DebugView.setTexture("regionDebug", regionDebug);
	}
	
	protected void drawDebugToScreen() {
		if(UI.valueToggle(CAMERA_DEBUG_3D)) {
			ImageUtil.cropFillCopyImage(regionDebug, p.g, false);
			if(activeRegion != null) {
				DemoAssets.setDemoFont(p.g);
				p.g.text("Editing: " + activeRegion.id(), 50, p.g.height - 150);
			}
		}
	}
	
	protected void drawApp() {
		// check depth camera stability
		if(FrameLoop.frameModMinutes(10)) P.out("Still running:", DebugView.uptimeStr());
		p.background(0);
		if(DepthCamera.instance().camera.isActive() == false) return;
		updateDepthRegions();
		addDebugTextures();
		drawDebugToScreen();
		updateKeystones();
		if(KeyboardState.keyTriggered('s')) saveRegions();
	}

	protected void saveRegions() {
		for (int i = 0; i < regions.length; i++) {
			regions[i].saveConfig();
		}
	}

	// IJoystickActiveDelegate methods 

	public void activeSwitched(BaseJoystick joystick, boolean value) {
		// P.out("activeSwitched() :: active = " + value);
		// get id of clicked region
		String regionId = ((DepthCameraRegion) joystick).id();
		if(value == true) P.out("CLICKED", regionId);
	}

	// IAppStoreListener methods

	public void updatedNumber(String key, Number val) {
		if(key.equals(REGION_INDEX)) {
			setRegionToUI(val.intValue());
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}

	// Keystoning ------------------------------------------------------

	protected PGraphicsKeystone[] keystoneQuads;

	protected void initKeystones() {
		keystoneQuads = new PGraphicsKeystone[] {
			new PGraphicsKeystone(p, pg, 12, FileUtil.getPath("text/keystoning/deph-region-demo-1.txt")),
			new PGraphicsKeystone(p, pg, 12, FileUtil.getPath("text/keystoning/deph-region-demo-2.txt")),
		};
	}

	protected void updateKeystones() {
		// enable/disable active keystoning quad
		if(KeyboardState.keyTriggered('1')) {
			keystoneQuads[0].setActive(true);
			keystoneQuads[1].setActive(false);
		} 
		if(KeyboardState.keyTriggered('2')) {
			keystoneQuads[0].setActive(false);
			keystoneQuads[1].setActive(true);
		}
		if(KeyboardState.keyTriggered('3')) {
			keystoneQuads[0].setActive(false);
			keystoneQuads[1].setActive(false);
		}

		// update & draw keystone quads
		for (int i = 0; i < keystoneQuads.length; i++) {
			keystoneQuads[i].update(p.g);
			keystoneQuads[i].fillSolidColor(p.g, regions[i].easedActive() ? 0xff00ff00 : 0xffff0000);
		}
	}
}
