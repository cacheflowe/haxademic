package com.haxademic.core.hardware.joystick;

public interface IJoystickCollection {
	public IJoystickControl getRegion( int index );
	public void update();
}
