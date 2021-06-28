package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.data.constants.PShapeTypes;
import com.haxademic.core.hardware.depthcamera.cameras.DepthCamera;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.hardware.joystick.BaseJoystick;
import com.haxademic.core.hardware.joystick.IJoystickControl;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;

public class DepthCameraRegion
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
	
	public void update() {
		update(null);
	}
	
	public void update(PGraphics debugGraphics) {
		IDepthCamera depthCamera = DepthCamera.instance().camera;
		
		float depthDivider = 0.3f;
        if(debugGraphics != null) {
        	debugGraphics.beginShape(PShapeTypes.QUADS);
    		debugGraphics.stroke(debugColor);
    		debugGraphics.fill( 255, pixelCount / minPixels * 10f );
        	debugGraphics.vertex(left, bottom, -near * depthDivider);
        	debugGraphics.vertex(right, bottom, -near * depthDivider);
        	debugGraphics.vertex(right, bottom, -far * depthDivider);
        	debugGraphics.vertex(left, bottom, -far * depthDivider);
        	debugGraphics.endShape();
        	debugGraphics.noStroke();
        }
        // find kinect readings in the region
		_isActive = false;
		if( depthCamera != null ) {
			pixelCount = 0;
			float controlXTotal = 0;
			float controlZTotal = 0;
			float pixelDepth = 0;
			for ( int x = left; x < right; x += pixelSkip ) {
				for ( int y = top; y < bottom; y += pixelSkip ) {
					pixelDepth = depthCamera.getDepthAt( x, y );
					if( pixelDepth != 0 && pixelDepth > near && pixelDepth < far ) {
				        if(debugGraphics != null) {
				        	debugGraphics.fill( debugColor, 127 );
				        	debugGraphics.pushMatrix();
				        	debugGraphics.translate(x, y, -pixelDepth * depthDivider);
				        	debugGraphics.box(pixelSkip, pixelSkip, pixelSkip);
				        	debugGraphics.popMatrix();
						}
						// add up for calculations
						pixelCount++;
						controlXTotal += x;
						controlZTotal += pixelDepth;
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
					float avgZ = controlZTotal / pixelCount;
					_controlZ = (MathUtil.getPercentWithinRange(near, far, avgZ) - 0.5f) * 2f;

					// show debug
			        if(debugGraphics != null) {
						debugGraphics.fill( 255, 127 );
						debugGraphics.pushMatrix();
						debugGraphics.translate(avgX, bottom - 250, -avgZ * depthDivider);
						debugGraphics.box(20, 500, 20);
						debugGraphics.popMatrix();
					}
				}
			}
		}
	}
}
