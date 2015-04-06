package com.haxademic.core.hardware.kinect;

import java.util.ArrayList;

import processing.core.PGraphics;
import processing.core.PImage;

import com.haxademic.core.app.P;
import com.haxademic.core.data.FloatBuffer;

public class KinectBufferedData {
	
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
	
	public KinectBufferedData( float pixelSize, float kinectClose, float kinectFar, int bufferFrames, boolean allowsEmptyData ) {
		_pixelSize = pixelSize;
		_allowsEmptyData = allowsEmptyData;
		_bufferFrames = bufferFrames;

		_kinectClose = kinectClose;
		_kinectFar = kinectFar;
		
		// default to use all of he kinect data
		_kinectLeft = 0;
		_kinectRight = KinectSize.WIDTH;
		_kinectTop = 0;
		_kinectBottom = KinectSize.HEIGHT;

		
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
		int offset = x + y * KinectSize.WIDTH;
		if( offset >= _bufferedData.length ) {
			return 0;
		} else {
			return _bufferedData[offset];
		}
	}
	
	public float getConfidenceForKinectPixel( int x, int y ) {
		int offset = x + y * KinectSize.WIDTH;
		if( offset >= _bufferedConfidenceData.length ) {
			return 0;
		} else {
			return _bufferedConfidenceData[offset];
		}
	}
	
	public PImage drawDebug() {
		if(_debugCanvas == null) {
			_debugCanvas = P.p.createGraphics(_kinectRight - _kinectLeft, _kinectBottom - _kinectTop, P.OPENGL);
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
	
	public void update( IKinectWrapper kinectWrapper ) {
		if(_bufferedData == null) {
			// use the same int array that the Kinect depth data uses, but only use indices that we care about, via _pixelSize 
			_bufferedData = kinectWrapper.getDepthData().clone();
			_bufferedConfidenceData = new float[_bufferedData.length];
		}
		
		int gridIndex = 0;
		float depth = 0;
		
		for ( int x = _kinectLeft; x < _kinectRight; x += _pixelSize ) {
			for ( int y = _kinectTop; y < _kinectBottom; y += _pixelSize ) {
				depth = kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				if( _allowsEmptyData == true || depth > 0 ) {
					_gridBuffer.get( gridIndex ).update( depth );
				}
				
				int offset = x + y * KinectSize.WIDTH;
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
}
