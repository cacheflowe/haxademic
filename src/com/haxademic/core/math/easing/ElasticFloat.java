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
	public float _speed;

	public float _value;
	public float _target;
		   
	public ElasticFloat( float val, float fric, float accel ) {
		_fric = fric;
		_accel = accel;

		_value = val;
		_target = val;
	}
	
	public float value() {
		return _value;
	}

	public void setCurrent( float val ) {
		_value = val;
	}

	public void setTarget( float target ) {
		_target = target;
	}

	public void setFriction( float fric ) {
		_fric = fric;
	}

	public void setAccel( float accel ) {
		_accel = accel;
	}

	public void update() {
		// update elastic point based on current target position vs current position
		_speed = ( ( _target - _value ) * _accel + _speed ) * _fric;
		_value += _speed;
	}

}



