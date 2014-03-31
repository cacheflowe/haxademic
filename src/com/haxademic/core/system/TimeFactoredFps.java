package com.haxademic.core.system;

import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PApplet;

public class TimeFactoredFps {
	
	protected PApplet p;
	protected float _targetFps;
	protected float _timeFactor;
	protected EasingFloat _timeFactorEased;
	protected float _actualFps;
	protected float _lastTime;
	
	public TimeFactoredFps( PApplet p, float targetFps ) {
		this.p = p;
		_targetFps = targetFps;
		_timeFactor = 1;
		_timeFactorEased = new EasingFloat(1, 20);
		_actualFps = targetFps;
		_lastTime = p.millis();
	}
	
	public float targetFps() {
		return _targetFps;
	}
	
	public float actualFps() {
		return _actualFps;
	}
	
	public float multiplier() {
		return _timeFactor;
	}
	
	public float multiplierEased() {
		return _timeFactorEased.value();
	}
	
	public void update() {
		_actualFps = 1000f / ( p.millis() - _lastTime );
		_timeFactor = _targetFps / _actualFps;
		_timeFactorEased.setTarget(_timeFactor);
		_timeFactorEased.update();
		_lastTime = p.millis();
	}
}	
