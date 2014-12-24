package com.haxademic.core.math.easing;

import com.haxademic.core.math.MathUtil;


public class EasingPowInterp
implements IEasingValue {
	
	public float _val, _low, _high, _progress, _easeFactor;
		   
	public EasingPowInterp( float value, float low, float high, float easeFactor ) {
		_progress = 0;
		_val = 0;
		_low = low;
		_high = high;
		_easeFactor = easeFactor;
	}
	
	public float value() {
		return MathUtil.interp(_low, _high, _val);
	}
	
	public void setTarget( float progress ) {
		_progress = progress;
	}
	
	public void setEaseFactor( float value ) {
		_easeFactor = value;
	}
	
	public void setHigh( float high ) {
		_high = high;
	}
	
	public void setLow( float low ) {
		_low = low;
	}
	
	public void update() {
		if (_progress < 0.5) 
			_val = 0.5f * (float)Math.pow(2*_progress, _easeFactor);
		else
			_val = 1 - 0.5f * (float)Math.pow(2*(1 - _progress), _easeFactor);
	}
	
}
