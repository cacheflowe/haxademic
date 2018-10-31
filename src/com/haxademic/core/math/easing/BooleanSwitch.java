package com.haxademic.core.math.easing;

public class BooleanSwitch {
	
	// callback interface
	
	public interface IBooleanSwitchCallback {
		public void booleanSwitched(BooleanSwitch booleanSwitch, boolean value);
	}
	
	// internal properties
	
	protected boolean value;
	protected boolean target;
	protected LinearFloat linearFloat;
	protected IBooleanSwitchCallback delegate;
	
	public BooleanSwitch(boolean value, int frames, IBooleanSwitchCallback delegate) {
		this(value, frames);
		this.delegate = delegate;
	}
	
	public BooleanSwitch(boolean value, int frames) {
		this.value = value;
		target = value;
		int initFrame = (value) ? 1 : 0;								// start at beginning (0 / false) or end (1 / true)
		linearFloat = new LinearFloat(initFrame, 1f / (float) frames);	// increment lerp towards target value
	}
	
	public boolean value() {
		return value;
	}
	
	public void value(boolean value) {
		this.value = value;
	}
	
	public float progress() {
		return linearFloat.value();
	}
	
	public boolean target() {
		return target;
	}
	
	public void target( boolean target ) {
		linearFloat.setTarget((target) ? 1 : 0);
	}
	
	public void setInc( float frames ) {
		linearFloat.setInc(1f / frames);
	}
		
	public void update() {
		linearFloat.update();
		if (linearFloat.value() == 0 && value == true) {
			setSwitchedValue(false);
		} else if (linearFloat.value() == 1 && value == false) {
			setSwitchedValue(true);
		} 
	}
	
	protected void setSwitchedValue(boolean newValue) {
		value = newValue;
		if (delegate != null) delegate.booleanSwitched(this, value);
	}

}
	