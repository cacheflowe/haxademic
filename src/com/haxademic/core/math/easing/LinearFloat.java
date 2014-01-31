package com.haxademic.core.math.easing;

public class LinearFloat 
implements IEasingValue {

	public float _val, _target, _inc;
		   
	public LinearFloat( float value, float inc ) {
		_val = value;
		_target = value;
		_inc = inc;
	}
	
	public float value() {
		return _val;
	}
	
	public float target() {
		return _target;
	}
	
	public void setCurrent( float value ) {
		_val = value;
	}
	
	public void setTarget( float value ) {
		_target = value;
	}
	
	public void setInc( float value ) {
		_inc = value;
	}
	
	public void update() {
		if( _val != _target ) {
			boolean switchedSides = false;
			if( _val < _target ) {
				_val += _inc;
				if( _val > _target ) switchedSides = true;
			} else {
				_val -= _inc;
				if( _val < _target ) switchedSides = true;
			}
			if( switchedSides == true ) {
				_val = _target;
			}
			
		}
	}

}
	