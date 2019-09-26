package com.haxademic.core.math.easing;

public class ElasticFloat
implements IEasingValue {
	
	// .75/.40 = bouncy elastic
	// .50/.90 = short elastic
	// .50/.50 = smooth elastic
	// .50/.30 = easing
	// .50/.30 = slow easing
	
	public float _fric;
	public float _accel;
	public float speed;

	public float value;
	public float _target;
		   
	public ElasticFloat( float val, float fric, float accel ) {
		_fric = fric;
		_accel = accel;

		value = val;
		_target = val;
	}
	
	public float value() {
		return value;
	}

	public IEasingValue setCurrent( float val ) {
		value = val;
		return this;
	}

	public IEasingValue setTarget( float target ) {
		_target = target;
		return this;
	}

	public IEasingValue setFriction( float fric ) {
		_fric = fric;
		return this;
	}

	public IEasingValue setAccel( float accel ) {
		_accel = accel;
		return this;
	}
	
	public boolean isComplete() {
		return value == _target;
	}

	public void update() {
		// update elastic point based on current target position vs current position
		speed = ( ( _target - value ) * _accel + speed ) * _fric;
		value += speed;
	}

}



