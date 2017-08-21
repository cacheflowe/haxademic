package com.haxademic.core.math.easing;

public class LinearFloat 
implements IEasingValue {

	public float _val, _target, _inc;
	public int _delay;
		   
	public LinearFloat( float value, float inc ) {
		_val = value;
		_target = value;
		_inc = inc;
		_delay = 0;
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
	
	public void setDelay( int frames ) {
		_delay = frames;
	}
	
	public void update() {
		if( _delay > 0 ) { _delay--; return; }
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
	