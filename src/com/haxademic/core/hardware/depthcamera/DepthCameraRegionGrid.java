package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.joystick.BaseJoysticksCollection;
import com.haxademic.core.hardware.joystick.IJoystickCollection;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PImage;

public class DepthCameraRegionGrid
extends BaseJoysticksCollection
implements IJoystickCollection {

	protected static PGraphics debugBuffer;
	protected int _kinectClose = 0;
	protected int _kinectFar = 0;
	protected int _kinectDepth = 0;

	// debug drawing helpers
	protected EasingFloat _sceneRot;
	protected EasingFloat _sceneTranslateY;
	protected EasingFloat _sceneTranslateZ;
	protected boolean _overheadView = false;
	protected int rows = 1;


	public DepthCameraRegionGrid(int cols, int rows, int kinectClose, int kinectFar, int padding, int kinectTop, int kinectBottom, int kinectPixelSkip, int minPixels) {
		super();
		
		this.rows = rows;
		_kinectClose = kinectClose;
		_kinectFar = kinectFar;
		_kinectDepth = _kinectFar - _kinectClose;

		// set up rectangles for position detection
		int colW = (DepthCameraSize.WIDTH - padding*(cols-1)) / cols;
		int kinectDepth = kinectFar - kinectClose;
		int rowH = (kinectDepth - padding*(rows-1)) / rows;

		for ( int x = 0; x < cols; x++ ) {
			for ( int y = 0; y < rows; y++ ) {
				DepthCameraRegion region = new DepthCameraRegion(
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

	public void update(boolean debug) {
		if(debug == false) {
			updateRegions();
		} else {
			if(debugBuffer == null) debugBuffer = P.p.createGraphics(DepthCameraSize.WIDTH, DepthCameraSize.HEIGHT, P.P3D);
			updateDebug();
		}
	}

	public void updateRegions() {
		for( int i=0; i < _joysticks.size(); i++ ) {
			_joysticks.get(i).update(debugBuffer);
		}
	}

	public void updateDebug() {
		debugBuffer.beginDraw();
		debugBuffer.background(0);

		debugBuffer.shininess(1000f);
		debugBuffer.lights();

		// lazy-init debugging camera easing
		if( _sceneRot == null ) {
			_sceneRot = new EasingFloat(0, 6f);
			_sceneTranslateY = new EasingFloat(0, 6f);
			_sceneTranslateZ = new EasingFloat(0, 6f);
		}

		// move scene towards front of kinect range
		debugBuffer.pushMatrix();
//		debugBuffer.translate(0,0,_kinectClose);

		// rotate scene for debugging
		_sceneTranslateY.update();
		_sceneRot.update();
		_sceneTranslateZ.update();

		debugBuffer.translate(0, _sceneTranslateY.value(), 0);
		debugBuffer.rotateX(_sceneRot.value());
		debugBuffer.translate(0, 0, _sceneTranslateZ.value());

		// loop through kinect data within rectangles ----------
		updateRegions();

		// draw regions' rectangles ----------------------------
		debugBuffer.pushMatrix();

		debugBuffer.rotateX(-P.PI/2f);
		debugBuffer.translate(0,0,0);

		for( int i=0; i < _joysticks.size(); i++ ) {
			_joysticks.get(i).drawDebug(debugBuffer);
		}
		debugBuffer.popMatrix();
		debugBuffer.popMatrix();

		debugBuffer.endDraw();
	}
	
	public PImage debugImage() {
		return debugBuffer;
	}

	public void toggleDebugOverhead() {
		if(debugBuffer == null) return;
		_overheadView = !_overheadView;

		if(_overheadView == true) {
			_sceneTranslateY.setTarget(debugBuffer.height * 1f);
			_sceneRot.setTarget(-P.PI * 0.5f);
			_sceneTranslateZ.setTarget(debugBuffer.height * 0.2f);
		} else {
			_sceneTranslateY.setTarget(0);
			_sceneRot.setTarget(0);
			_sceneTranslateZ.setTarget(0);
		}
	}

}