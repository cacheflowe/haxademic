package com.haxademic.core.hardware.kinect;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.FloatBuffer;

public class KinectAmbientActivityMonitor {
	
	protected float _pixelSize;
	protected float _kinectClose;
	protected float _kinectFar;
	protected ArrayList<FloatBuffer> _gridBuffer;
	protected float _activityLevel;
	
	public KinectAmbientActivityMonitor( float pixelSize, float kinectClose, float kinectFar ) {
		_pixelSize = pixelSize;
		_kinectClose = kinectClose;
		_kinectFar = kinectFar;
		_activityLevel = 0;
		buildGridBuffer();
	}
	
	protected void buildGridBuffer() {
		_gridBuffer = new ArrayList<FloatBuffer>();
		for ( int x = 0; x < KinectSize.WIDTH; x += _pixelSize ) {
			for ( int y = 0; y < KinectSize.HEIGHT; y += _pixelSize ) {
				_gridBuffer.add( new FloatBuffer(10) );
			}
		}
	}
	
	public float update( IKinectWrapper kinectWrapper, boolean debugDraw ) {
		int gridIndex = 0;
		float depth = 0;
		_activityLevel = 0;
		P.p.noStroke();
		for ( int x = 0; x < KinectSize.WIDTH; x += _pixelSize ) {
			for ( int y = 0; y < KinectSize.HEIGHT; y += _pixelSize ) {
				depth = kinectWrapper.getMillimetersDepthForKinectPixel( x, y );
				if( depth > 0 ) {
					_gridBuffer.get( gridIndex ).update( depth );
					_activityLevel += Math.abs( depth - _gridBuffer.get( gridIndex ).average() );
				}
				
				if( debugDraw == true ) {
					P.p.pushMatrix();
					P.p.fill(((_gridBuffer.get( gridIndex ).average() - _kinectClose) / (_kinectFar - _kinectClose)) * 255f);
					P.p.rect(x, y, _pixelSize, _pixelSize);
					P.p.popMatrix();
				}
				
				gridIndex += 1;
			}
		}
		return _activityLevel;
	}
}
