package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.joystick.BaseJoystick;
import com.haxademic.core.hardware.joystick.IJoystickControl;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class DepthCameraRegion
extends BaseJoystick
implements IJoystickControl {
	
	// region props
	protected int left = 0;
	protected int right = 0;
	protected int near = 0;
	protected int far = 0;
	protected int top = 0;
	protected int bottom = 0;
	protected int pixelSkip = 10;
	protected int minPixels = 20;
	
	protected int debugColor = -1;
	protected int pixelCount = 0;
	
	// ui
	protected String uiID = null;
	protected boolean hasUI = false;
	protected String CAMERA_LEFT = "CAMERA_LEFT";
	protected String CAMERA_RIGHT = "CAMERA_RIGHT";
	protected String CAMERA_NEAR = "CAMERA_NEAR";
	protected String CAMERA_FAR = "CAMERA_FAR";
	protected String CAMERA_TOP = "CAMERA_TOP";
	protected String CAMERA_BOTTOM = "CAMERA_BOTTOM";
	protected String CAMERA_PIXEL_SKIP = "CAMERA_PIXEL_SKIP";
	protected String CAMERA_MIN_PIXELS = "CAMERA_MIN_PIXELS";

	
	public DepthCameraRegion(String uiID, boolean savesUI) {
		this(0, 0, 0, 0, 0, 0, 0, 0, 0xff00ff00);

		// set UI keys to be unique in case of multiple cameras. needs testing
		String uiTitle = "DepthCamera Config | " + uiID;
		CAMERA_LEFT += "_" + uiID;
		CAMERA_RIGHT += "_" + uiID;
		CAMERA_NEAR += "_" + uiID;
		CAMERA_FAR += "_" + uiID;
		CAMERA_TOP += "_" + uiID;
		CAMERA_BOTTOM += "_" + uiID;
		CAMERA_PIXEL_SKIP += "_" + uiID;
		CAMERA_MIN_PIXELS += "_" + uiID;
		
		// if nothing passed in, we create UI controls
		UI.addTitle(uiTitle);
		UI.addSlider(CAMERA_LEFT, 0, 0, DepthCameraSize.WIDTH, 1, savesUI);
		UI.addSlider(CAMERA_RIGHT, DepthCameraSize.WIDTH, 0, DepthCameraSize.WIDTH, 1, savesUI);
		UI.addSlider(CAMERA_NEAR, 500, 0, 20000, 1, savesUI);
		UI.addSlider(CAMERA_FAR, 1200, 0, 20000, 1, savesUI);
		UI.addSlider(CAMERA_TOP, 0, 0, DepthCameraSize.HEIGHT, 1, savesUI);
		UI.addSlider(CAMERA_BOTTOM, DepthCameraSize.HEIGHT, 0, DepthCameraSize.HEIGHT, 1, savesUI);
		UI.addSlider(CAMERA_PIXEL_SKIP, 20, 1, 30, 1, savesUI);
		UI.addSlider(CAMERA_MIN_PIXELS, 20, 1, 200, 1, savesUI);
		
		// use default UI props & note that we have a UI
		updatePropsFromUI();
		this.uiID = uiID;
		hasUI = true;
	}
	
	public DepthCameraRegion(int left, int right, int near, int far, int top, int bottom, int pixelSkip, int minPixels, int debugColor) {
		this.left = left;
		this.right = right;
		this.near = near;
		this.far = far;
		this.top = top;
		this.bottom = bottom;
		this.pixelSkip = pixelSkip;
		this.minPixels = minPixels;
		this.debugColor = debugColor;
	}
	
	public int left() { return left; }
	public void left( int value ) { this.left = value; }
	public int right() { return right; }
	public void right( int value ) { this.right = value; }
	public int near() { return near; }
	public void near( int value ) { this.near = value; }
	public int far() { return far; }
	public void far( int value ) { this.far = value; }
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
		top(UI.valueInt(CAMERA_TOP));
		bottom(UI.valueInt(CAMERA_BOTTOM));
		pixelSkip(UI.valueInt(CAMERA_PIXEL_SKIP));
		minPixels(UI.valueInt(CAMERA_MIN_PIXELS));
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
        	debugGraphics.beginShape();
    		debugGraphics.stroke(debugColor);
    		debugGraphics.fill( 255, pixelCount / minPixels * 10f );
        	debugGraphics.vertex(left, bottom, -near * depthDivider);
        	debugGraphics.vertex(right, bottom, -near * depthDivider);
        	debugGraphics.vertex(right, bottom, -far * depthDivider);
        	debugGraphics.vertex(left, bottom, -far * depthDivider);
        	debugGraphics.endShape();
        	debugGraphics.noStroke();
        }
        // find depth readings in the region
		_isActive = false;
		if( depthCamera != null ) {
			pixelCount = 0;
			float controlXTotal = 0;
			float controlYTotal = 0;
			float controlZTotal = 0;
			float pixelDepth = 0;
			for ( int x = left; x < right; x += pixelSkip ) {
				for ( int y = top; y < bottom; y += pixelSkip ) {
					pixelDepth = depthCamera.getDepthAt( x, y );
					if( pixelDepth != 0 ) {
					    if(pixelDepth > near && pixelDepth < far) {
    				        if(debugGraphics != null) {
    				        	float debugZ = is3d ? -pixelDepth * depthDivider : 0;
    				        	debugGraphics.fill( debugColor, 127 );
    				        	debugGraphics.pushMatrix();
    				        	debugGraphics.translate(x, y, debugZ);
    				        	debugGraphics.box(pixelSkip, pixelSkip, pixelSkip);
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
                                debugGraphics.box(pixelSkip, pixelSkip, pixelSkip);
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
						debugGraphics.fill( 255, 127 );
						debugGraphics.pushMatrix();
						debugGraphics.translate(avgX, bottom - playerH/2, -avgZ * depthDivider);
						debugGraphics.box(20, playerH, 20);
						debugGraphics.popMatrix();
					}
				}
			}
		}
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

		for ( int x = left; x < right; x += pixelSkip ) {
			for ( int y = top; y < bottom; y += pixelSkip ) {
				pixelDepth = depthCamera.getDepthAt( x, y );
				if( pixelDepth != 0 && pixelDepth > near && pixelDepth < far ) {
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
		
		// draw bounds
    	debugGraphics.stroke(255, 0, 0);
    	debugGraphics.strokeWeight(4);
    	debugGraphics.noFill();
    	debugGraphics.rect(left, top, right - left, bottom - top);
		
		debugGraphics.endDraw();
	}
	
	////////////////////////////////////////////////////////////////////
	// UI controls
	////////////////////////////////////////////////////////////////////
	
	
}
