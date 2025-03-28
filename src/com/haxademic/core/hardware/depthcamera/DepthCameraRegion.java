package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.joystick.BaseJoystick;
import com.haxademic.core.hardware.joystick.IJoystickControl;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.net.JsonUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.data.JSONObject;

public class DepthCameraRegion
extends BaseJoystick
implements IJoystickControl {
	
	// region props
	protected int left = 0;
	protected int right = 0;
	protected int near = 0;
	protected int far = 0;
	protected int farBottom = 0;
	protected int top = 0;
	protected int bottom = 0;
	protected int pixelSkip = 10;
	protected int minPixels = 20;
	protected boolean farPlaneTilt = false;

	// debug	
	protected int debugColor = -1;
	protected boolean isEditing = false;
	protected String id;
	
	// active calculation
	protected int pixelCount = 0;
	protected boolean clickMode = false;
	
	// ui
	protected String uiID = null;
	protected boolean hasUI = false;

	public static final String LEFT = "CAMERA_LEFT";
	public static final String RIGHT = "CAMERA_RIGHT";
	public static final String NEAR = "CAMERA_NEAR";
	public static final String FAR = "CAMERA_FAR";
	public static final String FAR_BOTTOM = "CAMERA_FAR_BOTTOM";
	public static final String TOP = "CAMERA_TOP";
	public static final String BOTTOM = "CAMERA_BOTTOM";
	public static final String PIXEL_SKIP = "CAMERA_PIXEL_SKIP";
	public static final String MIN_PIXELS = "CAMERA_MIN_PIXELS";

	protected String CAMERA_LEFT = "CAMERA_LEFT";
	protected String CAMERA_RIGHT = "CAMERA_RIGHT";
	protected String CAMERA_NEAR = "CAMERA_NEAR";
	protected String CAMERA_FAR = "CAMERA_FAR";
	protected String CAMERA_FAR_BOTTOM = "CAMERA_FAR_BOTTOM";
	protected String CAMERA_TOP = "CAMERA_TOP";
	protected String CAMERA_BOTTOM = "CAMERA_BOTTOM";
	protected String CAMERA_PIXEL_SKIP = "CAMERA_PIXEL_SKIP";
	protected String CAMERA_MIN_PIXELS = "CAMERA_MIN_PIXELS";

	protected String configFilePath;
	
	public DepthCameraRegion() {
		this(null, false, false);
	}

	public DepthCameraRegion(String uiID) {
		this(uiID, false, false);
	}

	public DepthCameraRegion(String uiID, boolean savesUI) {
		this(uiID, savesUI, false);
	}

	public DepthCameraRegion(String uiID, boolean savesUI, boolean farPlaneTilt) {
		this(0, DepthCameraSize.WIDTH, 0, 5000, 0, DepthCameraSize.HEIGHT, 20, 20, 0xff00ff00);
		this.uiID = uiID;
		this.farPlaneTilt = farPlaneTilt;

		hasUI = uiID != null;
		if(hasUI) buildUI(savesUI);
	}
	
	public DepthCameraRegion(int left, int right, int near, int far, int top, int bottom, int pixelSkip, int minPixels, int debugColor) {
		this.left = left;
		this.right = right;
		this.near = near;
		this.far = far;
		this.farBottom = far;
		this.top = top;
		this.bottom = bottom;
		this.pixelSkip = pixelSkip;
		this.minPixels = minPixels;
		this.debugColor = debugColor;
	}

	protected void buildUI(boolean savesUI) {
		// set UI keys to be unique in case of multiple cameras. needs testing
		String uiTitle = "DepthCamera Config | " + uiID;
		CAMERA_LEFT = LEFT + "_" + uiID;
		CAMERA_RIGHT = RIGHT + "_" + uiID;
		CAMERA_NEAR = NEAR + "_" + uiID;
		CAMERA_FAR = FAR + "_" + uiID;
		CAMERA_FAR_BOTTOM = FAR_BOTTOM + "_" + uiID;
		CAMERA_TOP = TOP + "_" + uiID;
		CAMERA_BOTTOM = BOTTOM + "_" + uiID;
		CAMERA_PIXEL_SKIP = PIXEL_SKIP + "_" + uiID;
		CAMERA_MIN_PIXELS = MIN_PIXELS + "_" + uiID;
		
		String farBottomKey = (farPlaneTilt) ? CAMERA_FAR_BOTTOM : null;

		buildUI(uiTitle, CAMERA_LEFT, CAMERA_RIGHT, CAMERA_NEAR, CAMERA_FAR, farBottomKey, CAMERA_TOP, CAMERA_BOTTOM, CAMERA_PIXEL_SKIP, CAMERA_MIN_PIXELS, savesUI);

		// use default UI props & note that we have a UI
		updatePropsFromUI();
	}

	public static void buildUI(String title, String keyLeft, String keyRight, String keyNear, String keyFar, String keyFarBottom, String keyTop, String keyBottom, String keyPixelSkip, String keyMinPixels, boolean savesUI) {
		UI.addTitle(title);
		UI.addSlider(keyLeft, 0, 0, DepthCameraSize.WIDTH, 1, savesUI);
		UI.addSlider(keyRight, DepthCameraSize.WIDTH, 0, DepthCameraSize.WIDTH, 1, savesUI);
		UI.addSlider(keyNear, 500, 300, 10000, 1, savesUI);
		UI.addSlider(keyFar, 1200, 300, 10000, 1, savesUI);
		if (keyFarBottom != null) {
			UI.addSlider(keyFarBottom, 1200, 0, 10000, 1, savesUI);
		}
		UI.addSlider(keyTop, 0, 0, DepthCameraSize.HEIGHT, 1, savesUI);
		UI.addSlider(keyBottom, DepthCameraSize.HEIGHT, 0, DepthCameraSize.HEIGHT, 1, savesUI);
		UI.addSlider(keyPixelSkip, 20, 1, 30, 1, savesUI);
		UI.addSlider(keyMinPixels, 20, 1, 200, 1, savesUI);
	}

	public static void buildGenericUI() {
		buildUI("DepthCameraRegion", LEFT, RIGHT, NEAR, FAR, FAR_BOTTOM, TOP, BOTTOM, PIXEL_SKIP, MIN_PIXELS, false);
	}

	public static void setGenericUiFromRegion(DepthCameraRegion region) {
		UI.setValue(LEFT, region.left());
		UI.setValue(RIGHT, region.right());
		UI.setValue(NEAR, region.near());
		UI.setValue(FAR, region.far());
		UI.setValue(FAR_BOTTOM, region.farBottom());
		UI.setValue(TOP, region.top());
		UI.setValue(BOTTOM, region.bottom());
		UI.setValue(PIXEL_SKIP, region.pixelSkip());
		UI.setValue(MIN_PIXELS, region.minPixels());
	}
	
	// getters/setters in case we're not using UI
	
	public int left() { return left; }
	public void left( int value ) { this.left = value; }
	public int right() { return right; }
	public void right( int value ) { this.right = value; }
	public int near() { return near; }
	public void near( int value ) { this.near = value; }
	public int far() { return far; }
	public void far( int value ) { this.far = value; }
	public int farBottom() { return farBottom; }
	public void farBottom( int value ) { this.farBottom = value; }
	public int top() { return top; }
	public void top( int value ) { this.top = value; }
	public int bottom() { return bottom; }
	public void bottom( int value ) { this.bottom = value; }
	public int pixelSkip() { return pixelSkip; }
	public void pixelSkip( int value ) { this.pixelSkip = value; }
	public int minPixels() { return minPixels; }
	public void minPixels( int value ) { this.minPixels = value; }
	public int debugColor() { return debugColor; }
	public void debugColor( int value ) { this.debugColor = value; }
	public int pixelCount() { return pixelCount; }
	public void pixelCount( int value ) { this.pixelCount = value; }
	
	public String id() { 
		return id; 
	}
	public DepthCameraRegion id(String id) { 
		this.id = id; 
		return this; 
	}
	
	public DepthCameraRegion isEditing(boolean isEditing) { 
		this.isEditing = isEditing; 
		return this; 
	}
	
	public boolean isEditing() { 
		return isEditing; 
	}

	public DepthCameraRegion clickMode(boolean clickMode) { 
		this.clickMode = clickMode; 
		return this; 
	}
	
	public boolean clickMode() { 
		return clickMode; 
	}

	protected void updateSmoothedJoystickResults() {
		if(clickMode == true) {
			if(isActive()) {
				userActive.setInc(5);
			} else {
				userActive.setInc(10);
			}
		}
		super.updateSmoothedJoystickResults();
		
	}

	public void drawDebug(PGraphics debugGraphics) {
		if( debugColor == -1 ) return;
//		debugGraphics.stroke(debugColor);
//		debugGraphics.fill( debugColor, P.min(pixelCount * 5, 255) );
//		debugGraphics.rect(left, near, right - left, far - near);
	}
	
	public void updatePropsFromUI() {
		if(!hasUI) return;
		
		// ran into a case where a different camera size had been used, and got our of bounds errors in getDepthAt()
		// this hopefully ensures that switching camera sizes won't blow things up
		if(UI.value(CAMERA_RIGHT) >= DepthCameraSize.WIDTH) UI.setValue(CAMERA_RIGHT, DepthCameraSize.WIDTH);
		if(UI.value(CAMERA_BOTTOM) >= DepthCameraSize.HEIGHT) UI.setValue(CAMERA_BOTTOM, DepthCameraSize.HEIGHT);
			
		// set ui params
		left(UI.valueInt(CAMERA_LEFT));
		right(UI.valueInt(CAMERA_RIGHT));
		near(UI.valueInt(CAMERA_NEAR));
		far(UI.valueInt(CAMERA_FAR));
		farBottom((farPlaneTilt) ? UI.valueInt(CAMERA_FAR_BOTTOM) : UI.valueInt(CAMERA_FAR));
		top(UI.valueInt(CAMERA_TOP));
		bottom(UI.valueInt(CAMERA_BOTTOM));
		pixelSkip(UI.valueInt(CAMERA_PIXEL_SKIP));
		minPixels(UI.valueInt(CAMERA_MIN_PIXELS));
	}

	public void updatePropsFromGenericUI() {
		updatePropsFromGenericUI(false);
	}

	public void updatePropsFromGenericUI(boolean farPlaneTilt) {
		left(UI.valueInt(LEFT));
		right(UI.valueInt(RIGHT));
		near(UI.valueInt(NEAR));
		far(UI.valueInt(FAR));
		farBottom((farPlaneTilt) ? UI.valueInt(FAR_BOTTOM) : UI.valueInt(FAR));
		top(UI.valueInt(TOP));
		bottom(UI.valueInt(BOTTOM));
		pixelSkip(UI.valueInt(PIXEL_SKIP));
		minPixels(UI.valueInt(MIN_PIXELS));
	}
	
	public void debugLogPropsFromUI() {
		// debug info
		DebugView.setValue("Region "+uiID+" isActive", isActive());
		DebugView.setValue("pixelCount", pixelCount());
		DebugView.setValue("minPixels", minPixels());
		DebugView.setValue("controlX", controlX());
		DebugView.setValue("controlY", controlY());
		DebugView.setValue("controlZ", controlZ());
	}
	
	public void update() {
		update(null);
	}
	
	public void update(PGraphics debugGraphics) {
		update(null, debugGraphics, true);
	}
	
	
	public void update(IDepthCamera depthCamera, PGraphics debugGraphics) {
		update(depthCamera, debugGraphics, true);
	}
	
	public void update(IDepthCamera depthCamera, PGraphics debugGraphics, boolean is3d) {
		depthCamera = (depthCamera != null) ? depthCamera : DepthCamera.instance().camera;

		if(hasUI) updatePropsFromUI();
		
		// draw 3d "floor"
		float depthDivider = 0.3f;
		if(debugGraphics != null) {
			debugGraphics.push();
			debugGraphics.lights();

			debugGraphics.beginShape();
			debugGraphics.stroke(debugColor);
			debugGraphics.fill( 255, pixelCount / minPixels * 10f );
			debugGraphics.vertex(left, bottom, -near * depthDivider);
			debugGraphics.vertex(right, bottom, -near * depthDivider);
			debugGraphics.vertex(right, bottom, -far * depthDivider);
			debugGraphics.vertex(left, bottom, -far * depthDivider);
			debugGraphics.endShape();
			debugGraphics.noStroke();

			float boxW = right - left;
			float boxH = bottom - top;
			float boxD = (far - near) * depthDivider;
			float boxX = left + boxW/2;
			float boxY = top + boxH/2;
			float boxZ = -near * depthDivider - boxD/2;
			debugGraphics.push();
			PG.setDrawCenter(debugGraphics);
			debugGraphics.stroke(0, 255, 0);
			if(isEditing) debugGraphics.stroke(255);
			if(userActive.value()) debugGraphics.stroke(0, 0, 255);
			debugGraphics.noFill();
			debugGraphics.translate(boxX, boxY, boxZ);
			debugGraphics.box(boxW, boxH, boxD);
			debugGraphics.pop();
			// P.out(boxW, boxH, boxD, boxX, boxY, boxZ);
		}

		// find depth readings in the region
		_isActive = false;
		if( depthCamera != null ) {
			pixelCount = 0;
			float controlXTotal = 0;
			float controlYTotal = 0;
			float controlZTotal = 0;
			float pixelDepth = 0;
			float curFar = 0;
			for ( int x = left; x < right; x += pixelSkip ) {
				for ( int y = top; y < bottom; y += pixelSkip ) {
					pixelDepth = depthCamera.getDepthAt( x, y );
					if( pixelDepth != 0 ) {
						curFar = far;
						if(farPlaneTilt) {
							curFar = P.map(y, top, bottom, far, farBottom);
						}
						if(pixelDepth > near && pixelDepth < curFar) {
							if(debugGraphics != null) {
								float debugZ = is3d ? -pixelDepth * depthDivider : 0;
								debugGraphics.fill(debugColor, 127);
								debugGraphics.pushMatrix();
								debugGraphics.translate(x, y, debugZ);
								debugGraphics.rect(0, 0, pixelSkip, pixelSkip);
								debugGraphics.popMatrix();
							}
							// add up for calculations
							pixelCount++;
							controlXTotal += x;
							controlYTotal += y;
							controlZTotal += pixelDepth;
						} else {
							if(debugGraphics != null) {
								float debugZ = is3d ? -pixelDepth * depthDivider : 0;
								debugGraphics.fill( 127, 127 );
								debugGraphics.pushMatrix();
								debugGraphics.translate(x, y, debugZ);
								debugGraphics.rect(0, 0, pixelSkip, pixelSkip);
								debugGraphics.popMatrix();
							}
						}
					}
				}
			}

			// if we have enough blocks in a region, update the player's joystick position
			if( pixelCount > minPixels ) {
				_isActive = true;
				// compute averages
				if( controlXTotal > 0 && controlZTotal > 0 ) {
					float avgX = controlXTotal / pixelCount;
					_controlX = (MathUtil.getPercentWithinRange(left, right, avgX) - 0.5f) * 2f;
					float avgY = controlYTotal / pixelCount;
					_controlY = (MathUtil.getPercentWithinRange(top, bottom, avgY) - 0.5f) * 2f;
					float avgZ = controlZTotal / pixelCount;
					_controlZ = (MathUtil.getPercentWithinRange(near, far, avgZ) - 0.5f) * 2f;

					// show debug
					if(debugGraphics != null) {
						float playerH = debugGraphics.height * 0.75f;
						debugGraphics.fill(255);
						debugGraphics.pushMatrix();
						debugGraphics.translate(avgX, avgY, -avgZ * depthDivider);
						debugGraphics.box(50);
						debugGraphics.popMatrix();
					}
				}
			}

			if(debugGraphics != null) {
				debugGraphics.pop();
			}
		}

		updateSmoothedJoystickResults();
	}

	public void drawDebugFlat(PGraphics debugGraphics) {
		drawDebugFlat(null, debugGraphics);
	}
	
	public void drawDebugFlat(IDepthCamera depthCamera, PGraphics debugGraphics) {
		// get camera - singleton or pass one in
		depthCamera = (depthCamera != null) ? depthCamera : DepthCamera.instance().camera;

		// set up debug canvas
		debugGraphics.beginDraw();
		debugGraphics.background(0, 0);
		debugGraphics.noStroke();
		
		// check grid
		pixelCount = 0;
		float pixelDepth = 0;
		float curFar = far;

		for ( int x = left; x < right; x += pixelSkip ) {
			for ( int y = top; y < bottom; y += pixelSkip ) {
				pixelDepth = depthCamera.getDepthAt( x, y );
				if(pixelDepth != 0) {
					curFar = far;
					if(farPlaneTilt) {
						curFar = P.map(y, top, bottom, far, farBottom);
					}
					if(pixelDepth > near && pixelDepth < curFar) {
						if(debugGraphics != null) {
							debugGraphics.fill(debugColor, 255);
							debugGraphics.stroke(0);
							debugGraphics.rect(x, y, pixelSkip - 1, pixelSkip - 1);
						}
						// add up for calculations
						pixelCount++;
					}
				}
			}
		}
		
		// draw bounds
		debugGraphics.stroke(255, 0, 0);
		debugGraphics.strokeWeight(4);
		debugGraphics.noFill();
		debugGraphics.rect(left, top, right - left, bottom - top);
		
		debugGraphics.endDraw();
	}
	
	// save/load

	public void loadConfig(String configFilePath) {
		this.configFilePath = configFilePath;
		if(FileUtil.fileExists(configFilePath)) {
			JSONObject json = JsonUtil.jsonFromFile(configFilePath);
			left(json.getInt("left"));
			right(json.getInt("right"));
			near(json.getInt("near"));
			far(json.getInt("far"));
			int farBottom = json.hasKey("farBottom") ? json.getInt("farBottom") : json.getInt("far");
			farBottom(farBottom);
			top(json.getInt("top"));
			bottom(json.getInt("bottom"));
			pixelSkip(json.getInt("pixelSkip"));
			minPixels(json.getInt("minPixels"));
			P.out("DepthCameraRegion: Loaded DepthCameraRegion config from " + configFilePath);
			P.out(json.toString());
		} else {
			P.out("DepthCameraRegion: no config file found at " + configFilePath);
		}
	}
	
	public void saveConfig() {
		if(configFilePath == null) {
			P.out("DepthCameraRegion: no config file path set");
		} else {
			String dir = FileUtil.pathForFile(configFilePath);
			FileUtil.createDir(dir);
			P.out(dir, "exists", FileUtil.fileOrPathExists(dir));

			JSONObject json = new JSONObject();
			json.setInt("left", left);
			json.setInt("right", right);
			json.setInt("near", near);
			json.setInt("far", far);
			int farBottom = json.hasKey("farBottom") ? json.getInt("farBottom") : json.getInt("far");
			json.setInt("farBottom", farBottom);
			json.setInt("top", top);
			json.setInt("bottom", bottom);
			json.setInt("pixelSkip", pixelSkip);
			json.setInt("minPixels", minPixels);
			JsonUtil.jsonToFile(json, configFilePath);
			P.out("DepthCameraRegion: Saved DepthCameraRegion config to " + configFilePath);
		}
	}

}
