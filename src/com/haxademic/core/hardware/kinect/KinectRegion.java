package com.haxademic.core.hardware.kinect;

import processing.core.PGraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.joystick.IJoystickControl;
import com.haxademic.core.math.MathUtil;

public class KinectRegion
implements IJoystickControl {
	
	protected int _left = 0;
	protected int _right = 0;
	protected int _near = 0;
	protected int _far = 0;
	protected int _top = 0;
	protected int _bottom = 0;
	protected int _pixelSkip = 10;
	protected int _blockColor = -1;
	
	protected int _pixelCount = 0;
	protected int _minPixels = 20;
	protected boolean _isActive = false;
	protected float _controlX = 0;
	protected float _controlY = 0;
	protected float _controlZ = 0;
	
	public KinectRegion( int left, int right, int near, int far, int top, int bottom, int resolution, int minPixels, int blockColor ) {
		_left = left;
		_right = right;
		_near = near;
		_far = far;
		_top = top;
		_bottom = bottom;
		_pixelSkip = resolution;
		_minPixels = minPixels;
		_blockColor = blockColor;
	}
	
	public int pixelCount() {
		return _pixelCount;
	}
	
	public void pixelCount( int value ) {
		_pixelCount = value;
	}
	
	public float controlX() {
		return _controlX;
	}
	
	public void controlX( float value ) {
		_controlX = value;
	}
	
	public float controlY() {
		return 0;
	}
	
	public void controlY( float value ) {
		_controlY = value;
	}
	
	public float controlZ() {
		return _controlZ;
	}

	public void controlZ( float value ) {
		_controlZ = value;
	}
	
	public boolean isActive() {
		return _isActive;
	}
	
	public void isActive( boolean value ) {
		_isActive = value;
	}
	
	public void drawDebug(PGraphics debugGraphics) {
		if( _blockColor == -1 ) return;
		debugGraphics.stroke( _blockColor );
		debugGraphics.fill( _blockColor, P.min(_pixelCount * 5, 255) );
		debugGraphics.rect(_left, _near, _right - _left, _far - _near);
	}
	
	public void detect(PGraphics debugGraphics) {
		// find kinect readings in the region
		_isActive = false;
		if( P.p.kinectWrapper != null ) {
			_pixelCount = 0;
			float controlXTotal = 0;
			float controlZTotal = 0;
			float pixelDepth = 0;
			for ( int x = _left; x < _right; x += _pixelSkip ) {
				for ( int y = _top; y < _bottom; y += _pixelSkip ) {
					pixelDepth = P.p.kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
					if( pixelDepth != 0 && pixelDepth > _near && pixelDepth < _far ) {
				        if(debugGraphics != null) {
				        	debugGraphics.noStroke();
				        	debugGraphics.fill( _blockColor, 200 );
				        	debugGraphics.pushMatrix();
				        	debugGraphics.translate(x, y, -pixelDepth);
				        	debugGraphics.box(_pixelSkip, _pixelSkip, _pixelSkip);
				        	debugGraphics.popMatrix();
						}
						// add up for calculations
						_pixelCount++;
						controlXTotal += x;
						controlZTotal += pixelDepth;
					}
				}
			}

			// if we have enough blocks in a region, update the player's joystick position
			if( _pixelCount > _minPixels ) {
				_isActive = true;
				// compute averages
				if( controlXTotal > 0 && controlZTotal > 0 ) {
					float avgX = controlXTotal / _pixelCount;
					_controlX = (MathUtil.getPercentWithinRange(_left, _right, avgX) - 0.5f) * 2f;
					float avgZ = controlZTotal / _pixelCount;
					_controlZ = (MathUtil.getPercentWithinRange(_near, _far, avgZ) - 0.5f) * 2f;

					// show debug
			        if(debugGraphics != null) {
						debugGraphics.fill( 255 );
						debugGraphics.pushMatrix();
						debugGraphics.translate(avgX, 220, -avgZ);
						debugGraphics.box(40, 480, 40);
						debugGraphics.popMatrix();
					}
				}
			}
		}
	}
}
