package com.haxademic.core.math.easing;

public class EasingFloat
implements IEasingValue {
	
	public float _val, _target, _easeFactor, _speed;
	protected float COMPLETE_THRESHOLD = 0.001f;
		   
	public EasingFloat( float value, float easeFactor ) {
		_val = value;
		_target = value;
		_easeFactor = easeFactor;
		_speed = 0f;
	}
	
	public float value() {
		return _val;
	}
	
	public float target() {
		return _target;
	}
	
	public void setCurrent( float value ) {
		_val = value;
		_target = value;
	}
	
	public void setTarget( float value ) {
		_target = value;
	}
	
	public void setEaseFactor( float value ) {
		_easeFactor = value;
	}
	
	public void setCompleteThreshold( float value ) {
		COMPLETE_THRESHOLD = value;
	}
	
	public void update() {
		if(_val == _target) return;
		_val += (_target - _val ) / _easeFactor;
		checkThreshold();
	}
	
	public void update(boolean accelerates) {
		// don't do any math if we're already at the destination
		if(_val == _target) return;
		// interpolate
		if(accelerates == false) {
			update();
		} else {
			float increment = (_target - _val ) / _easeFactor;
			if(Math.abs(increment) > Math.abs(_speed)) {
				_speed += increment / _easeFactor;
				increment = _speed;
			} else {
				_speed = increment;
			}
			_val += increment;
		}
		// set the _value to the target if we're close enough
		checkThreshold();
	}

	protected void checkThreshold() {
		if( Math.abs( _val - _target ) < COMPLETE_THRESHOLD ) _val = _target;
	}
}
