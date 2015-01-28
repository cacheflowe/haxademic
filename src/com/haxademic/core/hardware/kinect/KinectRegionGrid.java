package com.haxademic.core.hardware.kinect;

import processing.core.PGraphics;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.joystick.BaseJoysticksCollection;
import com.haxademic.core.hardware.joystick.IJoystickCollection;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

public class KinectRegionGrid
extends BaseJoysticksCollection
implements IJoystickCollection {
	
	protected PGraphics _pg;
	protected int _kinectClose = 0;
	protected int _kinectFar = 0;
	protected int _kinectDepth = 0;
	
	// debug drawing helpers
	protected EasingFloat _sceneRot;
	protected EasingFloat _sceneTranslateZ;
	protected EasingFloat _sceneTranslateZ2;
	protected boolean _overheadView = false;


	public KinectRegionGrid(int cols, int rows, int kinectClose, int kinectFar, int padding, int kinectTop, int kinectBottom, int kinectPixelSkip, int minPixels) {
		this(cols, rows, kinectClose, kinectFar, padding, kinectTop, kinectBottom, kinectPixelSkip, minPixels, false);
	}
	

	public KinectRegionGrid(int cols, int rows, int kinectClose, int kinectFar, int padding, int kinectTop, int kinectBottom, int kinectPixelSkip, int minPixels, boolean debug) {
		super();
		
		if(debug == true) {
			_pg = P.p.createGraphics(KinectWrapper.KWIDTH, KinectWrapper.KHEIGHT, P.OPENGL);
		}
		
		_kinectClose = kinectClose;
		_kinectFar = kinectFar;
		_kinectDepth = _kinectFar - _kinectClose;
		
		// set up rectangles for position detection
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
						P.p.color( MathUtil.randRange(130,255), MathUtil.randRange(130,255), MathUtil.randRange(130,255) )
				);
				_joysticks.add( region );
			}
		}
	}
	
	public void update() {
		if(_pg == null) {
			updateRegions();			
		} else {
			updateDebug();
		}
	}
	
	public void updateRegions() {
		for( int i=0; i < _joysticks.size(); i++ ) {
			_joysticks.get(i).detect(_pg);
		}
	}
	
	public void updateDebug() {
		_pg.beginDraw();
		_pg.clear();
		
		_pg.shininess(1000f); 
		_pg.lights();

		// lazy-init debugging camera easing
		if( _sceneRot == null ) {
			_sceneRot = new EasingFloat(0, 6f);
			_sceneTranslateZ = new EasingFloat(0, 6f);
			_sceneTranslateZ2 = new EasingFloat(0, 6f);
		}
		
		// move scene towards front of kinect range
		_pg.pushMatrix();
		_pg.translate(0,0,_kinectClose);
		
		// rotate scene for debugging
		_sceneTranslateZ.update();
		_sceneRot.update();
		_sceneTranslateZ2.update();
		
		_pg.translate(0,0,_sceneTranslateZ.value());
		_pg.rotateX(_sceneRot.value());
		_pg.translate(0,0,_sceneTranslateZ2.value());
		
		// loop through kinect data within rectangles ----------
		updateRegions();
		
		// draw regions' rectangles ----------------------------
		_pg.pushMatrix();
		
		_pg.rotateX(-P.PI/2f);
		_pg.translate(0,0,460);
				
		for( int i=0; i < _joysticks.size(); i++ ) {
			_joysticks.get(i).drawDebug(_pg);
		}
		_pg.popMatrix();
		_pg.popMatrix();

		_pg.endDraw();
	}
	
	public void toggleDebugOverhead() {
		if(_pg == null) return;
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
	
	public void drawDebug(PGraphics pg) {
		if(_pg == null) return;
		pg.image(_pg, 0, 0);
	}

}