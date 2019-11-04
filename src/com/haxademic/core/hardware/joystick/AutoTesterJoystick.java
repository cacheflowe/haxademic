package com.haxademic.core.hardware.joystick;

import processing.core.PGraphics;

class AutoTesterJoystick
extends BaseJoystick
implements IJoystickControl {
	
	protected float _oscSpeedX = 0;
	protected float _oscSpeedY = 0;
	protected float _oscSpeedZ = 0;
	protected float _oscCurX = 0;
	protected float _oscCurY = 0;
	protected float _oscCurZ = 0;
	
	public AutoTesterJoystick( float oscSpeedX, float oscSpeedY, float oscSpeedZ ) {
		_oscSpeedX = oscSpeedX;
		_oscSpeedY = oscSpeedY;
		_oscSpeedZ = oscSpeedZ;
	}
	
	public void drawDebug(PGraphics debugGraphics) {}
	
	public void update(PGraphics debugGraphics) {
		_oscCurX += _oscSpeedX;
    	_controlX = (float) Math.sin( _oscCurX );
    	_oscCurY += _oscSpeedY;
    	_controlY = (float) Math.sin( _oscCurY );
    	_oscCurZ += _oscSpeedZ;
    	_controlZ = (float) Math.sin( _oscCurZ );
	}
}
