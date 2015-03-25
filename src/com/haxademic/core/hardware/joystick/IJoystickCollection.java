package com.haxademic.core.hardware.joystick;

import processing.core.PGraphics;

public interface IJoystickCollection {
	public IJoystickControl getRegion( int index );
	public void update();
	public void drawDebug(PGraphics pg);
}
