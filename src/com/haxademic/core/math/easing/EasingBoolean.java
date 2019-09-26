package com.haxademic.core.math.easing;

public class EasingBoolean {
	
	// callback interface
	
	public interface IEasingBooleanCallback {
		public void booleanSwitched(EasingBoolean booleanSwitch, boolean value);
	}
	
	// internal properties
	
	protected boolean value;
	protected boolean target;
	protected LinearFloat linearFloat;
	protected IEasingBooleanCallback delegate;
	
	public EasingBoolean(boolean value, int frames, IEasingBooleanCallback delegate) {
		this(value, frames);
		this.delegate = delegate;
	}
	
	public EasingBoolean(boolean value, int frames) {
		this.value = value;
		target = value;
		int initFrame = (value) ? 1 : 0;								// start at beginning (0 / false) or end (1 / true)
		linearFloat = new LinearFloat(initFrame, 1f / (float) frames);	// increment lerp towards target value
	}
	
	public boolean value() {
		return value;
	}
	
	public EasingBoolean value(boolean value) {
		this.value = value;
		return this;
	}
	
	public float progress() {
		return linearFloat.value();
	}
	
	public boolean target() {
		return target;
	}
	
	public EasingBoolean target( boolean target ) {
		linearFloat.setTarget((target) ? 1 : 0);
		return this;
	}
	
	public EasingBoolean setInc( float frames ) {
		linearFloat.setInc(1f / frames);
		return this;
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
	