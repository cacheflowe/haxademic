package com.haxademic.core.hardware.kinect;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.math.easing.EasingFloat;

public class KinectRegionGrid {
	
	protected PAppletHax p;
	protected int _kinectClose = 0;
	protected int _kinectFar = 0;
	protected int _kinectDepth = 0;
	
	public ArrayList<KinectRegion> kinectRegions;
	
	// debug drawing helpers
	protected EasingFloat _sceneRot;
	protected EasingFloat _sceneTranslateZ;
	protected EasingFloat _sceneTranslateZ2;
	protected boolean _overheadView = false;


	public KinectRegionGrid(PAppletHax p, int cols, int rows, int kinectClose, int kinectFar, int padding, int kinectTop, int kinectBottom, int kinectPixelSkip, int minPixels) {
		this.p = p;
		
		_kinectClose = kinectClose;
		_kinectFar = kinectFar;
		_kinectDepth = _kinectFar - _kinectClose;
		
		// set up rectangles for position detection
		kinectRegions = new ArrayList<KinectRegion>();
		int colW = (KinectWrapper.KWIDTH - padding*(cols-1)) / cols;
		int kinectDepth = kinectFar - kinectClose;
		int rowH = (kinectDepth - padding*(rows-1)) / rows;

		for ( int x = 0; x < cols; x++ ) {
			for ( int y = 0; y < rows; y++ ) {
				KinectRegion region = new KinectRegion(
						colW * x + padding * x, 
						colW * x + padding * x + colW, 
						kinectClose + y * rowH + padding * y,
						kinectClose + y * rowH + padding * y + rowH,
						kinectTop,
						kinectBottom,
						kinectPixelSkip,
						minPixels,
						p.color( p.random(130,255), p.random(130,255), p.random(130,255) )
				);
				kinectRegions.add( region );
			}
		}
	}
	
	public KinectRegion getRegion( int index ) {
		return kinectRegions.get(index);
	}
	
	public void update() {
		updateRegions(false);
	}
	
	public void updateRegions( boolean isDebugging ) {
		for( int i=0; i < kinectRegions.size(); i++ ) {
			kinectRegions.get(i).detect(isDebugging);
		}
	}
	
	public void updateDebug() {
		// lazy-init debugging camera easing
		if( _sceneRot == null ) {
			_sceneRot = new EasingFloat(0, 6f);
			_sceneTranslateZ = new EasingFloat(0, 6f);
			_sceneTranslateZ2 = new EasingFloat(0, 6f);
		}
		
		// move scene towards front of kinect range
		p.pushMatrix();
		p.translate(0,0,_kinectClose);
		
		// rotate scene for debugging
		_sceneTranslateZ.update();
		_sceneRot.update();
		_sceneTranslateZ2.update();
		
		p.translate(0,0,_sceneTranslateZ.value());
		p.rotateX(_sceneRot.value());
		p.translate(0,0,_sceneTranslateZ2.value());
		
		// loop through kinect data within rectangles ----------
		updateRegions(true);
		
		// draw regions' rectangles ----------------------------
		p.pushMatrix();
		
		p.rotateX(-P.PI/2f);
		p.translate(0,0,460);
				
		for( int i=0; i < kinectRegions.size(); i++ ) {
			kinectRegions.get(i).drawRect();
		}
		p.popMatrix();
		p.popMatrix();
	}
	
	public void toggleDebugOverhead() {
		_overheadView = !_overheadView;
		
		if(_overheadView == true) {
			_sceneTranslateZ.setTarget(_kinectDepth * -2f);
			_sceneRot.setTarget(-P.PI/2f);
			_sceneTranslateZ2.setTarget(_kinectClose + _kinectDepth);
		} else {
			_sceneTranslateZ.setTarget(0);
			_sceneRot.setTarget(0);
			_sceneTranslateZ2.setTarget(0);
		}

	}
}