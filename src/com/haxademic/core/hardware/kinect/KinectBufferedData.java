package com.haxademic.core.hardware.kinect;

import java.util.ArrayList;

import com.haxademic.core.data.FloatBuffer;

public class KinectBufferedData {
	
	protected float _pixelSize;
	protected float _kinectClose;
	protected float _kinectFar;
	protected ArrayList<FloatBuffer> _gridBuffer;
	protected int[] _bufferedData;
	
	public KinectBufferedData( float pixelSize, float kinectClose, float kinectFar, int bufferSize ) {
		_pixelSize = pixelSize;
		_kinectClose = kinectClose;
		_kinectFar = kinectFar;
		buildGridBuffer(bufferSize);
	}
	
	protected void buildGridBuffer(int bufferSize) {
		_gridBuffer = new ArrayList<FloatBuffer>();
		for ( int x = 0; x < KinectSize.WIDTH; x += _pixelSize ) {
			for ( int y = 0; y < KinectSize.HEIGHT; y += _pixelSize ) {
				_gridBuffer.add( new FloatBuffer(bufferSize) );
			}
		}
	}
	
	public void update( IKinectWrapper kinectWrapper ) {
		if(_bufferedData == null) {
			_bufferedData = kinectWrapper.getDepthData().clone();
		}
		
		int gridIndex = 0;
		float depth = 0;
		
		for ( int x = 0; x < KinectSize.WIDTH; x += _pixelSize ) {
			for ( int y = 0; y < KinectSize.HEIGHT; y += _pixelSize ) {
				depth = kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
//				if( depth > 0 ) {
					_gridBuffer.get( gridIndex ).update( depth );
//				}
				
				int offset = x + y * KinectSize.WIDTH;
				if( offset < _bufferedData.length ) {
					_bufferedData[offset] = (int) _gridBuffer.get( gridIndex ).average();
				}

				gridIndex += 1;
			}
		}
	}
	
	public int getBufferedDepthForKinectPixel( int x, int y ) {
		int offset = x + y * KinectSize.WIDTH;
		if( offset >= _bufferedData.length ) {
			return 0;
		} else {
			return _bufferedData[offset];
		}
	}
}
