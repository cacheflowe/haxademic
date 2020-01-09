package com.haxademic.core.math.easing;

public class LinearFloat 
implements IEasingValue {

	protected float value;
	protected float target;
	protected float inc;
	protected int delay;
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
	
	public static float incForFrames(int frames) {
		return 1f / frames;
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
	
	public IEasingValue setCurrent(float value) {
		this.value = value;
		return this;
	}
	
	public IEasingValue setTarget(float target) {
		this.target = target;
		if(target != value) complete = false;
		return this;
	}
	
	public IEasingValue setDelegate( IEasingValueDelegate delegate ) {
		this.delegate = delegate;
		return this;
	}
	
	public IEasingValue setInc(float inc) {
		this.inc = inc;
		return this;
	}
	
	public IEasingValue setDelay(int frames) {
		this.delay = frames;
		return this;
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
			delegate.easingValueComplete(this);
		}
	}

}
	