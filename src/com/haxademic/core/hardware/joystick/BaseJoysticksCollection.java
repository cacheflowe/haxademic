package com.haxademic.core.hardware.joystick;

import java.util.ArrayList;

import processing.core.PGraphics;

public class BaseJoysticksCollection
implements IJoystickCollection {
	
	public ArrayList<IJoystickControl> _joysticks;
	
	public BaseJoysticksCollection() {
		_joysticks = new ArrayList<IJoystickControl>();
	}
	
	public BaseJoysticksCollection createJoysticks(int numJoysticks) {
		for ( int i = 0; i < numJoysticks; i++ ) {
			BaseJoystick region = new BaseJoystick();
			_joysticks.add( region );
		}
		return this;
	}
	
	public IJoystickControl getRegion( int index ) {
		return _joysticks.get(index);
	}
	
	public void update() {}
	
	public void updateRegions() {}
	
	public void drawDebug(PGraphics pg) {}
}
