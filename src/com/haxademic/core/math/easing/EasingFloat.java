package com.haxademic.core.math.easing;

import com.haxademic.core.app.P;

public class EasingFloat
implements IEasingValue {
	
	protected float value;
	protected float target;
	protected float easeFactor;
	protected float speed;
	protected int delay = 0;
	protected float completeThreshold = 0.0001f;
	
	protected boolean complete = false;
	protected IEasingValueDelegate delegate;
		   
	public EasingFloat(float value, float easeFactor) {
		this(value, easeFactor, null);
	}
	
	public EasingFloat(float value, float easeFactor, IEasingValueDelegate delegate) {
		this.value = value;
		this.target = value;
		this.easeFactor = (easeFactor <= 1) ? 1f / easeFactor : easeFactor;
		this.delegate = delegate;
		speed = 0f;
		delay = 0;
	}
	
	public float value() {
		return value;
	}
	
	public float target() {
		return target;
	}
	
	public float easeFactor() {
		return easeFactor;
	}
	
	public IEasingValue setCurrent( float value ) {
		this.value = value;
		this.target = value;
		return this;
	}
	
	public IEasingValue setTarget( float target ) {
		this.target = target;
		if(target != value) complete = false;
		return this;
	}
	
	public IEasingValue setEaseFactor( float value ) {
		this.easeFactor = (value <= 1) ? 1f / value : value;
		return this;
	}
	
	public IEasingValue setDelay( int frames ) {
		this.delay = frames;
		return this;
	}
	
	public IEasingValue setCompleteThreshold( float value ) {
		completeThreshold = value;
		return this;
	}
	
	public boolean isComplete() {
		return value == target;
	}
	
	public void update() {
		if(value == target) return;
		if(delay > 0) { delay--; return; }
		value += (target - value ) / easeFactor;
		checkThreshold();
		if(delegate != null) checkComplete();
	}
	
	public void update(boolean accelerates) {
		// don't do any math if we're already at the destination
		if(value == target) return;
		if(delay > 0) { delay--; return; }
		// interpolate
		if(accelerates == false) {
			update();
		} else {
			float increment = (target - value ) / easeFactor;
			if(Math.abs(increment) > Math.abs(speed)) {
				speed += increment / easeFactor;
				increment = speed;
			} else {
				speed = increment;
			}
			value += increment;
		}
		// set the value to the target if we're close enough
		checkThreshold();
		if(delegate != null) checkComplete();
	}
	
	public void updateRadians() {
		if(value == target) return;
		if(delay > 0) { delay--; return; }
		float angleDifference = target - value;
		float addToLoop = 0;
		if( angleDifference > Math.PI) {
			addToLoop = -P.TWO_PI;
		} else if(angleDifference < -Math.PI ) {
			addToLoop = P.TWO_PI;
		}
		value += ((target - value + addToLoop) / easeFactor);
		if(Math.abs( value - target ) < completeThreshold) {
			value = target;
		}
	}

	protected void checkThreshold() {
		if( Math.abs( value - target ) < completeThreshold ) value = target;
	}
	
	protected void checkComplete() {
		P.p.debugView.setValue("value", value);
		P.p.debugView.setValue("target", target);
		P.p.debugView.setValue("complete", complete);
		if(complete == false && value == target) {
			complete = true;
			delegate.complete(this);
		}
	}
}
