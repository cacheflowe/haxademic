package com.haxademic.core.hardware.joystick;

import processing.core.PGraphics;

public class BaseJoystick
implements IJoystickControl {
	
	protected float _oscSpeedX = 0;
	protected float _oscSpeedY = 0;
	protected float _oscSpeedZ = 0;
	protected float _oscCurX = 0;
	protected float _oscCurY = 0;
	protected float _oscCurZ = 0;
	
	protected boolean _isActive = true;
	protected float _controlX = 0;
	protected float _controlY = 0;
	protected float _controlZ = 0;
	
	public BaseJoystick() {}
	
	public float controlX() {
		return _controlX;
	}
	
	public void controlX( float value ) {
		_controlX = value;
	}
	
	public float controlY() {
		return _controlY;
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
	
	public void drawDebug(PGraphics debugGraphics) {}
	
	public void update(PGraphics debugGraphics) {}
}
