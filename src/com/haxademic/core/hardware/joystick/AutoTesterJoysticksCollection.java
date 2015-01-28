package com.haxademic.core.hardware.joystick;

import processing.core.PGraphics;

import com.haxademic.core.math.MathUtil;

public class AutoTesterJoysticksCollection
extends BaseJoysticksCollection
implements IJoystickCollection {
		
	public AutoTesterJoysticksCollection(int numJoysticks) {
		super();
		for ( int i = 0; i < numJoysticks; i++ ) {
			AutoTesterJoystick region = new AutoTesterJoystick(MathUtil.randRangeDecimal(0.005f,0.1f), MathUtil.randRangeDecimal(0.005f,0.1f), MathUtil.randRangeDecimal(0.005f,0.1f));
			_joysticks.add( region );
		}
	}
	
	public void update() {
		updateRegions();			
	}
	
	public void updateRegions() {
		for( int i=0; i < _joysticks.size(); i++ ) {
			_joysticks.get(i).detect(null);
		}
	}
	
	public void drawDebug(PGraphics pg) {}
}
