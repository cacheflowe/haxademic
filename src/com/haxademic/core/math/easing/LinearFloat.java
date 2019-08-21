package com.haxademic.core.math.easing;

import com.haxademic.core.app.P;

public class LinearFloat 
implements IEasingValue {

	public float value;
	public float target;
	public float inc;
	public int delay;
	
	protected boolean complete = false;
	protected IEasingValueDelegate delegate;

	public LinearFloat(float value, float inc) {
		this(value, inc, null);
	}
	
	public LinearFloat(float value, float inc, IEasingValueDelegate delegate) {
		this.value = value;
		this.target = value;
		this.inc = inc;
		this.delegate = delegate;
		delay = 0;
	}
	
	public float value() {
		return value;
	}
	
	public float target() {
		return target;
	}
	
	public float inc() {
		return inc;
	}
	
	public void setCurrent(float value) {
		this.value = value;
	}
	
	public void setTarget(float target) {
		this.target = target;
		if(target != value) complete = false;
	}
	
	public void setInc(float inc) {
		this.inc = inc;
	}
	
	public void setDelay(int frames) {
		this.delay = frames;
	}
	
	// mask to be swappable with EasingFloat
	public void update(boolean bool) {
		update();
	}
	
	public boolean isComplete() {
		return value == target;
	}
	
	public void update() {
		if( delay > 0 ) { delay--; return; }
		if( value != target ) {
			boolean passedTarget = false;
			if( value < target ) {
				value += inc;
				if( value > target ) passedTarget = true;
			} else {
				value -= inc;
				if( value < target ) passedTarget = true;
			}
			if(passedTarget == true) {
				value = target;
				if(delegate != null) checkComplete();
			}
		}
	}
	
	protected void checkComplete() {
		if(complete == false && value == target) {
			complete = true;
			delegate.complete(this);
		}
	}

}
	