package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.cv.ColorObjectDetection;
import com.haxademic.core.hardware.joystick.BaseJoystick;
import com.haxademic.core.hardware.joystick.IJoystickControl;

import processing.core.PGraphics;

public class DepthCameraOverheadRegion
extends BaseJoystick
implements IJoystickControl {
	
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
	
	protected PGraphics depthSilhouette;
	protected PGraphics cameraRegionBuffer;
	protected ColorObjectDetection colorObjectDetection;
	
	public DepthCameraOverheadRegion(PGraphics depthSilhouette, int left, int right, int top, int bottom, int pixelSkip, int minPixels, int debugColor) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.pixelSkip = pixelSkip;
		this.minPixels = minPixels;
		this.debugColor = debugColor;
		
		// store mini silhouette buffer
		this.depthSilhouette = depthSilhouette;
		cameraRegionBuffer = PG.newPG(right - left, bottom - top);
		
		// init color object detection 
		colorObjectDetection = new ColorObjectDetection(cameraRegionBuffer, 1f);
		colorObjectDetection.colorClosenessThreshold(0.1f);
		colorObjectDetection.minPointsThreshold(minPixels);
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
		// draw rectangle in debug buffer to show 
		// where we're pulling player zone from 
		debugGraphics.push();
		debugGraphics.noFill();
		debugGraphics.stroke(255, 0, 0);
		debugGraphics.strokeWeight(1);
		debugGraphics.rect(left, top, right - left, bottom - top);
		debugGraphics.pop();
		
		// save to DebugView
		DebugView.setTexture("cameraRegionBuffer_"+left+"-"+top, cameraRegionBuffer);
	}
	
	public void update() {
		update(null);
	}
	
	public void update(PGraphics debugGraphics) {
		// copy player's tiny rectangle from silhouette buffer
		cameraRegionBuffer.copy(depthSilhouette, 
			left, top, right-left, bottom-top, 
			0, 0, cameraRegionBuffer.width, cameraRegionBuffer.height
		);
		
		// update color object detector
		boolean isDebugging = debugGraphics != null;
		colorObjectDetection.colorClosenessThreshold(0.95f);
		colorObjectDetection.update(cameraRegionBuffer);
		colorObjectDetection.debugging(isDebugging);
		DebugView.setTexture("cameraAnalysisBuffer_"+left+"-"+top, colorObjectDetection.analysisBuffer());
		
        // take active reading from color object detection
		_isActive = colorObjectDetection.isActive();
		_controlX = 2f * (colorObjectDetection.x() - 0.5f);	// normalize to a unit value (-1 to +1)
		_controlZ = 2f * (colorObjectDetection.y() - 0.5f);
	}
}
