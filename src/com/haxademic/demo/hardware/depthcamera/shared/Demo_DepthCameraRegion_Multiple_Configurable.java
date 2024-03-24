package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.DepthCameraRegion;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera.DepthCameraType;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.hardware.joystick.BaseJoystick;
import com.haxademic.core.hardware.joystick.IJoystickControl.IJoystickActiveDelegate;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;

public class Demo_DepthCameraRegion_Multiple_Configurable
extends PAppletHax
implements IAppStoreListener, IJoystickActiveDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// TODO: 
	// - Add name/id for regions to check active?
	// - Actively-editing region should be highlighted in UI
	// - Test with floor alignment & measureing tape
	// - Active trigger should be faster on than off!! make this configurable

	// regions
	protected DepthCameraRegion[] regions;
	protected DepthCameraRegion activeRegion;

	// debug
	protected PGraphics regionDebug;		  // updated by the `region` object
	protected PGraphics regionFlatDebug;	// updated by the `region` object
	protected PGraphics joystickDebug;		// updated by the `region`
	
	// UI
	protected String REGION_INDEX = "REGION_INDEX";
	protected String CAMERA_DEBUG_3D = "CAMERA_DEBUG_3D";

	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.DEPTH_CAM_RGB_ACTIVE, false);
	}
	
	protected void firstFrame() {
		initCameras();
		buildRegions();
		initDebugBuffers();
		P.store.addListener(this);
	}

	protected void buildRegions() {
		// build multiple regions
		regions = new DepthCameraRegion[] {
				new DepthCameraRegion(),
				new DepthCameraRegion(),
				new DepthCameraRegion(),
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
		activeRegion = regions[index];
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
		joystickDebug = PG.newPG(200, 200);
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
		// draw x/y coords grid debug view
		DepthCameraRegion.drawDebugCoords(joystickDebug, activeRegion.easedX(), activeRegion.easedY(), activeRegion.easedActive());
	}
		
	protected void addDebugTextures() {
		if(DepthCamera.instance().camera.getRgbImage() != null) DebugView.setTexture("depthCamera.getRgbImage", DepthCamera.instance().camera.getRgbImage());
		if(DepthCamera.instance().camera.getDepthImage() != null) DebugView.setTexture("depthCamera.getDepthImage", DepthCamera.instance().camera.getDepthImage());
		if(UI.valueToggle(CAMERA_DEBUG_3D)) DebugView.setTexture("regionDebug", regionDebug);
		if(UI.valueToggle(CAMERA_DEBUG_3D)) DebugView.setTexture("joystickDebug", joystickDebug);
	}
	
	protected void drawDebugToScreen() {
		if(UI.valueToggle(CAMERA_DEBUG_3D)) {
			ImageUtil.cropFillCopyImage(regionDebug, p.g, false);
		}

		// draw joystick x/y debug in upper corner
		// use userY if overhead vs mirror
		p.g.image(joystickDebug, p.width - joystickDebug.width, 0);
	}
	
	protected void drawApp() {
		// check depth camera stability
		if(FrameLoop.frameModMinutes(10)) P.out("Still running:", DebugView.uptimeStr());
		p.background(0);
		if(DepthCamera.instance().camera.isActive() == false) return;
		updateDepthRegions();
		addDebugTextures();
		drawDebugToScreen();
		if(KeyboardState.keyTriggered('s')) saveRegions();
	}

	protected void saveRegions() {
		for (int i = 0; i < regions.length; i++) {
			regions[i].saveConfig();
		}
	}

	// IJoystickActiveDelegate methods 

	public void activeSwitched(BaseJoystick joystick, boolean value) {
		P.out("activeSwitched() :: active = " + value);
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
}
