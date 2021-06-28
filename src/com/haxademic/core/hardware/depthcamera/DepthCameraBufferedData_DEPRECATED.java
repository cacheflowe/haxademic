package com.haxademic.core.hardware.depthcamera;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;
import com.haxademic.core.math.easing.FloatBuffer;

import processing.core.PGraphics;
import processing.core.PImage;

public class DepthCameraBufferedData_DEPRECATED {
	
	protected float _pixelSize;
	protected float _kinectClose;
	protected float _kinectFar;
	protected int _kinectLeft;
	protected int _kinectRight;
	protected int _kinectTop;
	protected int _kinectBottom;
	protected ArrayList<FloatBuffer> _gridBuffer;
	protected int[] _bufferedData;
	protected float[] _bufferedConfidenceData;
	protected int _bufferFrames;
	protected boolean _allowsEmptyData = false;
	protected PGraphics _debugCanvas;
	
	public DepthCameraBufferedData_DEPRECATED( float pixelSize, float kinectClose, float kinectFar, int bufferFrames, boolean allowsEmptyData ) {
		_pixelSize = pixelSize;
		_allowsEmptyData = allowsEmptyData;
		_bufferFrames = bufferFrames;

		_kinectClose = kinectClose;
		_kinectFar = kinectFar;
		
		// default to use all of he kinect data
		_kinectLeft = 0;
		_kinectRight = DepthCameraSize.WIDTH;
		_kinectTop = 0;
		_kinectBottom = DepthCameraSize.HEIGHT;

		
		buildGridBuffer(_bufferFrames);
	}
	
	public void setKinectRect(int kinectLeft, int kinectRight, int kinectTop, int kinectBottom) {
		_kinectLeft = kinectLeft;
		_kinectRight = kinectRight;
		_kinectTop = kinectTop;
		_kinectBottom = kinectBottom;
		buildGridBuffer(_bufferFrames); // TODO: make this part of another constructor?
	}
	
	public int getBufferedDepthForKinectPixel( int x, int y ) {
		int offset = x + y * DepthCameraSize.WIDTH;
		if( offset >= _bufferedData.length ) {
			return 0;
		} else {
			return _bufferedData[offset];
		}
	}
	
	public float getConfidenceForKinectPixel( int x, int y ) {
		int offset = x + y * DepthCameraSize.WIDTH;
		if( offset >= _bufferedConfidenceData.length ) {
			return 0;
		} else {
			return _bufferedConfidenceData[offset];
		}
	}
	
	public PImage drawDebug() {
		if(_debugCanvas == null) {
			_debugCanvas = P.p.createGraphics(_kinectRight - _kinectLeft, _kinectBottom - _kinectTop, P.P3D);
		}
		
		_debugCanvas.beginDraw();
		_debugCanvas.background(100,0,0);
		_debugCanvas.noStroke();
//		int gridIndex = 0;
		for ( int x = _kinectLeft; x < _kinectRight; x += _pixelSize ) {
			for ( int y = _kinectTop; y < _kinectBottom; y += _pixelSize ) {
//				int depth = getBufferedDepthForKinectPixel( x, y );
				float confidence = getConfidenceForKinectPixel( x, y );
				if( confidence > 0.25f ) {
					_debugCanvas.fill(255);
//					_debugCanvas.fill(255f * confidence);
					_debugCanvas.rect(x - _kinectLeft, y - _kinectTop, _pixelSize, _pixelSize);
				}
//				gridIndex++;
			}
		}
		_debugCanvas.endDraw();
		return _debugCanvas;
	}
	
	protected void buildGridBuffer(int bufferSize) {
		_gridBuffer = new ArrayList<FloatBuffer>();
		for ( int x = _kinectLeft; x < _kinectRight; x += _pixelSize ) {
			for ( int y = _kinectTop; y < _kinectBottom; y += _pixelSize ) {
				_gridBuffer.add( new FloatBuffer(bufferSize) );
			}
		}
	}
	
	public void update( IDepthCamera kinectWrapper ) {
		if(_bufferedData == null) {
			// use the same int array that the Kinect depth data uses, but only use indices that we care about, via _pixelSize 
			_bufferedData = kinectWrapper.getDepthData().clone();
			_bufferedConfidenceData = new float[_bufferedData.length];
		}
		
		int gridIndex = 0;
		float depth = 0;
		
		for ( int x = _kinectLeft; x < _kinectRight; x += _pixelSize ) {
			for ( int y = _kinectTop; y < _kinectBottom; y += _pixelSize ) {
				depth = kinectWrapper.getDepthAt( x, y );
				if( _allowsEmptyData == true || depth > 0 ) {
					_gridBuffer.get( gridIndex ).update( depth );
				}
				
				int offset = x + y * DepthCameraSize.WIDTH;
				float confidence = 0;
//				if( offset < _bufferedData.length ) {
					confidence = _gridBuffer.get( gridIndex ).confidence();
					_bufferedConfidenceData[offset] = confidence;
					// ony include data from buffers that aren't flickering off (& thus have a zero in their array)
					if(_allowsEmptyData == true && confidence < 0.25f) {
						_bufferedData[offset] = 0;
					} else {
						_bufferedData[offset] = (int) _gridBuffer.get( gridIndex ).max();
					}
//				}

				gridIndex += 1;
			}
		}
	}
	
	public void extraSpread() {
		int gridIndex = 0;
		for ( int x = _kinectLeft; x < _kinectRight; x += _pixelSize ) {
			for ( int y = _kinectTop; y < _kinectBottom; y += _pixelSize ) {
				int offset = x + y * DepthCameraSize.WIDTH;
				float confidence = _gridBuffer.get( gridIndex ).confidence();
				if(confidence == 0) {
					int prevOffset = x + Math.round(y - _pixelSize) * DepthCameraSize.WIDTH;
					int nextOffset = x + Math.round(y + _pixelSize) * DepthCameraSize.WIDTH;

					// grab prev/next value in column if current pixel is empty
					if(y != _kinectTop && _bufferedConfidenceData[nextOffset] != 0) {
						_bufferedConfidenceData[offset] = _bufferedConfidenceData[nextOffset];
						_bufferedData[offset] = _bufferedData[nextOffset];
					} else if(y != _kinectBottom && _bufferedConfidenceData[prevOffset] != 0) {
						_bufferedConfidenceData[offset] = _bufferedConfidenceData[prevOffset];
						_bufferedData[offset] = _bufferedData[prevOffset];
					}
				}
				
				gridIndex += 1;
			}
		}
	}
}
