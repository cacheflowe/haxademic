package com.haxademic.demo.hardware.depthcamera.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.store.IAppStoreListener;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.DepthCameraRegion;
import com.haxademic.core.hardware.depthcamera.DepthCameraSize;
import com.haxademic.core.hardware.depthcamera.cameras.RealSenseWrapper;
import com.haxademic.core.hardware.joystick.BaseJoystick;
import com.haxademic.core.hardware.joystick.IJoystickControl.IJoystickActiveDelegate;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.media.DemoAssets;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONArray;

public class Demo_DepthCameraRegion_Multiple_RealSenseWrapper
extends PAppletHax
implements IAppStoreListener, IJoystickActiveDelegate {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	// cameras
	protected RealSenseWrapper camera1;
	protected RealSenseWrapper camera2;
	protected RealSenseWrapper[] cameras;
	protected RealSenseWrapper activeCamera;
	protected int cameraStartIndex;

	// regions
	protected DepthCameraRegion[] regions;
	protected DepthCameraRegion activeRegion;
	protected int[] cameraIndexPerRegion;

	// debug buffer - updated by the `activeRegion`
	protected PGraphics regionDebug;
	
	// UI
	protected String CAMERA_INDEX = "CAMERA_INDEX";
	protected String REGION_INDEX = "REGION_INDEX";
	protected String CAMERA_DEBUG_3D = "CAMERA_DEBUG_3D";


	protected void config() {
		Config.setAppSize(1024, 1024);
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	////////////////////////////////////////////////////////////////////
	// Frame loop
	////////////////////////////////////////////////////////////////////

	protected void firstFrame() {
		String[] serials = new String[] { "220222067397", "146322076817" };
		init(serials, 0); // 2nd set of cameras will start at index: 3
	}

	protected void init(String[] cameraSerials, int cameraStartIndex) {
		this.cameraStartIndex = cameraStartIndex;
		initCameras(cameraSerials);
		buildRegions(cameraStartIndex);
		initUI(); // after cameras/regions are created for dynamic slider
		initDebugBuffers();
		P.store.addListener(this);
	}

	protected void initCameras(String[] cameraSerials) {
		// RealSenseWrapper.setTinyStream();
		RealSenseWrapper.setSmallStream();
		RealSenseWrapper.METERS_FAR_THRESH = 4f;

		// init cameras & add depth textures to DebugView
		cameras = new RealSenseWrapper[cameraSerials.length];
		for (int i = 0; i < cameraSerials.length; i++) {
			cameras[i] = new RealSenseWrapper(p, false, true, cameraSerials[i]);
			DebugView.setTexture("camera" + i, cameras[i].getDepthImage());
		}
	}

	public String regionIdForColumn(int columnIndex, int clickIndex) {
		return "REGION_" + columnIndex + "_" + clickIndex;
	}

	protected void buildRegions(int startIndex) {
		// build shared UI for region settings
		DepthCameraRegion.buildGenericUI();

		// build multiple regions
		// 5 regions per columns - 4 clicks + 1 presence detection region
		int numColumns = 4;
		int regionsPerColumn = 5;
		int numRegions = numColumns * regionsPerColumn;
		regions = new DepthCameraRegion[numRegions];
		cameraIndexPerRegion = new int[numRegions];
		for (int i = startIndex; i < (numRegions + startIndex); i++) {
			int columnIndex = P.floor(i / regionsPerColumn);
			int regionInColumn = i % regionsPerColumn;
			String regionId = regionIdForColumn(columnIndex, regionInColumn);
			P.out("Building region w/regionId:", regionId);

			// - set defaults - we'll override with config files
			// - regions have loadConfig() and we have a separate camera indexes config file
			// - first 4 regions in each column are click mode, and the last is presence detection.
			//   ...also turn on far-bottom tilt extra param for floor plane
			boolean isPresence = regionInColumn == 4;
			regions[i] = new DepthCameraRegion(null, false, isPresence);
			regions[i].id(regionId);
			regions[i].clickMode(!isPresence);
			cameraIndexPerRegion[i] = 0; 
		}

		// set active region
		activeRegion = null;

		// set active callback delegate for all regions
		for (int i = 0; i < regions.length; i++) {
			regions[i].setActiveDelegate(this); // callback for easing boolean for active state
			regions[i].updatePropsFromGenericUI(); // set default UI values
			String configFile = P.path("text/json/depth-camera-regions/"+regions[i].id()+".json");
			regions[i].loadConfig(configFile);
		}

		// load camera index per region from config file
		// if it doesn't exist, save the default values so we have a file
		if(!FileUtil.fileExists(camerasConfigFile())) saveCameraIndexesConfig();
		// then load values from file into local array
		JSONArray cameraIndexes;
		try {
			String[] fileLines = FileUtil.readTextFromFile(camerasConfigFile());
			String jsonStr = String.join("\n", fileLines);
			P.out("loaded camera indexes config:", jsonStr);
			cameraIndexes = JSONArray.parse(jsonStr);
			for (int i = 0; i < cameraIndexes.size(); i++) { // copy json config into local array
				cameraIndexPerRegion[i] = cameraIndexes.getInt(i);
			}
		} catch (Exception e) {
			P.out(new Object[]{"JSONArray.parse() failed in buildRegions()"});
		}

		// load first region into UI - this immediately starts updating the region
		setRegionToUI(-1);
	}


	protected void initUI() {
		// add ui sliders to tweak at runtime
		UI.addTitle("DepthCameraRegion Config");
		UI.addSlider(CAMERA_INDEX, 0, 0, cameras.length - 1, 1, false);
		UI.addSlider(REGION_INDEX, -1, -1, regions.length - 1, 1, false);
		UI.addToggle(CAMERA_DEBUG_3D, true, false);
	}

	protected void initDebugBuffers() {
		regionDebug = PG.newPG(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT);
	}
	
	////////////////////////////////////////////////////////////////////
	// Frame loop
	////////////////////////////////////////////////////////////////////
	
	protected void drawApp() {
		// check depth camera stability
		p.background(0);
		// if(DepthCamera.instance().camera.isActive() == false) return;
		updateDepthRegions();
		drawDebugToScreen();
		// jey commands
		if(KeyboardState.keyTriggered('s')) saveRegions();
		if(KeyboardState.keyTriggered('r')) resetRegion();
	}

	protected void updateDepthRegions() {
		// update cameras
		for (int i = 0; i < cameras.length; i++) {
			cameras[i].update();
		}

		// apply UI controls - get current camera, region, and debug graphic
		if (activeRegion != null) {
			boolean isPresence = activeRegion.id().charAt(activeRegion.id().length() - 1) == '4'; // if last character is '4', it's a presence region
			DebugView.setValue("isPresence", isPresence);
			activeRegion.updatePropsFromGenericUI(isPresence);
		}
		activeCamera = cameras[UI.valueInt(CAMERA_INDEX)];
		boolean showDebug = UI.valueToggle(CAMERA_DEBUG_3D);

		// update depth data on all regions, and display the actively-editing region
		for (int i = 0; i < regions.length; i++) {
			if (showDebug && i == UI.valueInt(REGION_INDEX)) {
				regionDebug.beginDraw();
				regionDebug.background(0);
				// update region and show depth data/config 
				regions[i].update(activeCamera, regionDebug); // debug display active region
				// draw extra info
				regionDebug.push();
				DemoAssets.setDemoFont(regionDebug);
				regionDebug.text(activeRegion.id(), 50, regionDebug.height - 50);
				regionDebug.pop();
				regionDebug.endDraw();
			} else {
				// update region, always
				RealSenseWrapper camera = cameras[cameraIndexPerRegion[i]];
				regions[i].update(camera, null);
			}
		}
		// if not editing, show a message
		if(showDebug == true && activeRegion == null) {
			regionDebug.beginDraw();
			regionDebug.background(0);
			regionDebug.push();
			DemoAssets.setDemoFont(regionDebug);
			regionDebug.text("No active region", 50, regionDebug.height - 50);
			regionDebug.pop();
			regionDebug.endDraw();
		}
	}

	protected void drawDebugToScreen() {
		if (UI.valueToggle(CAMERA_DEBUG_3D)) {
			ImageUtil.cropFillCopyImage(regionDebug, p.g, false);
		}
	}

	////////////////////////////////////////////////////////////////////
	// Region management
	////////////////////////////////////////////////////////////////////

	protected void setRegionToUI(int index) {
		// deactivate prior region
		if (activeRegion != null) activeRegion.isEditing(false);

		// activate new region and set current props on shared UI
		if (index >= 0) {
			// activate region on UI and set as current
			activeRegion = regions[index];
			activeRegion.isEditing(true);
			DepthCameraRegion.setGenericUiFromRegion(activeRegion);
			// set camera index for this region
			UI.setValue(CAMERA_INDEX, cameraIndexPerRegion[index]);
		} else {
			activeRegion = null;
		}
	}

	protected void resetRegion() {
		if(activeRegion != null) {
			UI.setValue(DepthCameraRegion.LEFT, 0);
			UI.setValue(DepthCameraRegion.RIGHT, DepthCameraSize.WIDTH);
			UI.setValue(DepthCameraRegion.TOP, 0);
			UI.setValue(DepthCameraRegion.BOTTOM, DepthCameraSize.HEIGHT);
			UI.setValue(DepthCameraRegion.NEAR, 500);
			UI.setValue(DepthCameraRegion.FAR, 3000);
			UI.setValue(DepthCameraRegion.FAR_BOTTOM, 3000);
			UI.setValue(DepthCameraRegion.PIXEL_SKIP, 10);
			UI.setValue(DepthCameraRegion.MIN_PIXELS, 20);
		}
	}

	////////////////////////////////////////////////////////////////////
	// File saving/loading
	////////////////////////////////////////////////////////////////////

	protected void saveRegions() {
		for (int i = 0; i < regions.length; i++) {
			regions[i].saveConfig();
		}
		saveCameraIndexesConfig();
	}

	protected String camerasConfigFile() {
		return P.path("text/json/depth-camera-regions/camera-indexes-"+cameraStartIndex+".json");
	}

	protected void saveCameraIndexesConfig() {
		// convert local array to JSON
		JSONArray json = new JSONArray();
		for (int i = 0; i < cameraIndexPerRegion.length; i++) {
			json.append(cameraIndexPerRegion[i]);
		}
		JsonUtil.jsonToFile(json.toString(), camerasConfigFile());
		P.out("Saved camera indexes config:", json.toString());
	}

	////////////////////////////////////////////////////////////////////
	// IJoystickActiveDelegate methods
	////////////////////////////////////////////////////////////////////

	public void activeSwitched(BaseJoystick joystick, boolean value) {
		if (value == true) {
			// User clicked on a region!
			String regionId = ((DepthCameraRegion) joystick).id();
			P.store.setString("DEPTH_REGION_CLICKED", regionId);
			P.out("CLICKED", regionId);
		}
	}

	////////////////////////////////////////////////////////////////////
	// IAppStoreListener methods
	////////////////////////////////////////////////////////////////////

	public void updatedNumber(String key, Number val) {
		// when a region index is changed, update the active region that we're editing
		if(key.equals(REGION_INDEX)) {
			setRegionToUI(val.intValue());
			// saveRegions();
		}
		// when a camera index is changed, update the local cameraIndex array, making sure there's an active region to associate the camera with
		if(key.equals(CAMERA_INDEX)) {
			int activeRegionIndex = UI.valueInt(REGION_INDEX);
			if(activeRegionIndex >= 0) {
				cameraIndexPerRegion[activeRegionIndex] = val.intValue();
				// saveRegions();
			}
		}
	}
	public void updatedString(String key, String val) {}
	public void updatedBoolean(String key, Boolean val) {}
	public void updatedImage(String key, PImage val) {}
	public void updatedBuffer(String key, PGraphics val) {}
}
