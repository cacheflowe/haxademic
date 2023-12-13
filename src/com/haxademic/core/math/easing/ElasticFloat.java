package com.haxademic.core.math.easing;

public class ElasticFloat
implements IEasingValue {
	
		// Hooke's law: F = -kx
	// .75/.40 = bouncy elastic
	// .50/.90 = short elastic
	// .50/.50 = smooth elastic
	// .50/.30 = easing
	// .50/.30 = slow easing
	
	protected float fric;
	protected float accel;
	protected float speed;
	protected float value;
	protected float target;
			
	public ElasticFloat( float val, float fric, float accel ) {
		this.fric = fric;
		this.accel = accel;
		this.value = val;
		this.target = val;
	}
	
	public float value() {
		return value;
	}

	public IEasingValue setCurrent( float val ) {
		value = val;
		return this;
	}

	public IEasingValue setTarget( float target ) {
		this.target = target;
		return this;
	}
	
	public IEasingValue setDelegate( IEasingValueDelegate delegate ) {
		// no-op :-/ hard to know when elastic value settles, but should be revisited
		return this;
	}

	public IEasingValue setFriction( float fric ) {
		this.fric = fric;
		return this;
	}

	public IEasingValue setAccel( float accel ) {
		this.accel = accel;
		return this;
	}
	
	public boolean isComplete() {
		return value == target;
	}

	public IEasingValue update() {
		// update elastic point based on current target position vs current position
		speed = ( ( target - value ) * accel + speed ) * fric;
		value += speed;
		return this;
	}

}



