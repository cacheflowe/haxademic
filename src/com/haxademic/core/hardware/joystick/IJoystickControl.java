package com.haxademic.core.hardware.joystick;

import processing.core.PGraphics;

public interface IJoystickControl {
	public float controlX();
	public void controlX(float value);
	public float controlY();
	public void controlY(float value);
	public float controlZ();
	public void controlZ(float value);
	public boolean isActive();
	public void isActive( boolean value );
	public void drawDebug(PGraphics debugGraphics);
	public void update(PGraphics debugGraphics);
}
