package com.haxademic.core.system;

import com.haxademic.core.app.P;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PApplet;

public class TimeFactoredFps {
	
	protected float targetFps;
	protected float timeFactor;
	protected EasingFloat timeFactorEased;
	protected float actualFps;
	protected float lastTime;
	
	public TimeFactoredFps( PApplet p, float targetFps ) {
		this.targetFps = targetFps;
		timeFactor = 1;
		timeFactorEased = new EasingFloat(1, 20);
		actualFps = targetFps;
		lastTime = p.millis();
	}
	
	public float targetFps() {
		return targetFps;
	}
	
	public float actualFps() {
		return actualFps;
	}
	
	public float multiplier() {
		return timeFactor;
	}
	
	public float multiplierEased() {
		return timeFactorEased.value();
	}
	
	public void update() {
		actualFps = 1000f / ( P.p.millis() - lastTime );
		timeFactor = targetFps / actualFps;
		timeFactorEased.setTarget(timeFactor);
		timeFactorEased.update();
		lastTime = P.p.millis();
	}
}	
