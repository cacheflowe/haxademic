package com.haxademic.core.math.easing;

import com.haxademic.core.app.P;

public class EasingFloat
implements IEasingValue {
	
	public float val, target, easeFactor, speed;
	protected int delay = 0;
	protected float completeThreshold = 0.0001f;
		   
	public EasingFloat( float value, float easeFactor ) {
		val = value;
		target = value;
		this.easeFactor = (easeFactor <= 1) ? 1f / easeFactor : easeFactor;
		speed = 0f;
		delay = 0;
	}
	
	public float value() {
		return val;
	}
	
	public float target() {
		return target;
	}
	
	public void setCurrent( float value ) {
		val = value;
		target = value;
	}
	
	public void setTarget( float value ) {
		target = value;
	}
	
	public void setEaseFactor( float value ) {
		this.easeFactor = (value <= 1) ? 1f / value : value;
	}
	
	public void setDelay( int frames ) {
		delay = frames;
	}
	
	public void setCompleteThreshold( float value ) {
		completeThreshold = value;
	}
	
	public boolean isComplete() {
		return val == target;
	}
	
	public void update() {
		if(val == target) return;
		if(delay > 0) { delay--; return; }
		val += (target - val ) / easeFactor;
		checkThreshold();
	}
	
	public void update(boolean accelerates) {
		// don't do any math if we're already at the destination
		if(val == target) return;
		if(delay > 0) { delay--; return; }
		// interpolate
		if(accelerates == false) {
			update();
		} else {
			float increment = (target - val ) / easeFactor;
			if(Math.abs(increment) > Math.abs(speed)) {
				speed += increment / easeFactor;
				increment = speed;
			} else {
				speed = increment;
			}
			val += increment;
		}
		// set the value to the target if we're close enough
		checkThreshold();
	}
	
	public void updateRadians() {
		if(val == target) return;
		if(delay > 0) { delay--; return; }
		float angleDifference = target - val;
		float addToLoop = 0;
		if( angleDifference > Math.PI) {
			addToLoop = -P.TWO_PI;
		} else if(angleDifference < -Math.PI ) {
			addToLoop = P.TWO_PI;
		}
		val += ((target - val + addToLoop) / easeFactor);
		if(Math.abs( val - target ) < completeThreshold) {
			val = target;
		}
	}

	protected void checkThreshold() {
		if( Math.abs( val - target ) < completeThreshold ) val = target;
	}
}
