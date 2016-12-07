package com.haxademic.core.math.easing;

public class EasingFloat
implements IEasingValue {
	
	public float _val, _target, _easeFactor;
	protected float COMPLETE_THRESHOLD = 0.001f;
		   
	public EasingFloat( float value, float easeFactor ) {
		_val = value;
		_target = value;
		_easeFactor = easeFactor;
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
		_val -= ( ( _val - _target ) / _easeFactor );
		if( Math.abs( _val - _target ) < COMPLETE_THRESHOLD ) _val = _target;
	}
	
}
