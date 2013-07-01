package com.haxademic.core.data;

public class FloatRange {
	public float _min, _max, _easeFactor;
	   
	public FloatRange( float min, float max ) {
		_min = min;
		_max = max;
	}
	
	public void set( float min, float max ) {
		_min = min;
		_max = max;
	}

	public float min() {
		return _min;
	}

	public float max() {
		return _max;
	}

	public float center() {
		return _min + ( _max - _min ) / 2;
	}

}
