package com.haxademic.core.data.patterns;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class PatternNoise
implements ISequencerPattern {

	protected float thresh = 0.6f; 
	protected int maxSteps = 8;
	protected float noiseSpeed = 2.1f;
	protected float noiseStart = 0;

	public PatternNoise() {
		this(0.6f, 8);
	}
	
	public PatternNoise(float thresh) {
		this(thresh, 8);
	}
	
	public PatternNoise(int maxSteps) {
		this(0.6f, maxSteps);
	}

	public PatternNoise(float thresh, int maxSteps) {
		this.thresh = thresh;
		this.maxSteps = maxSteps;
	}
	

	@Override
	public void newPattern(boolean[] steps) {
		int numSteps = steps.length;
		// new noise() props
		noiseSpeed = MathUtil.randRangeDecimal(0.1f, 20f);
		noiseStart = MathUtil.randRangeDecimal(0, 1000);
		
		// mouse control debug
		/**
		noiseSpeed = Mouse.xNorm * 20f;
		noiseStart = Mouse.yNorm * 1000;
		P.p.debugView.setValue("noiseSpeed", noiseSpeed);
		P.p.debugView.setValue("noiseStart", noiseStart);
		*/
		
		// apply to steps
		for (int i = 0; i < numSteps; i++) {
			steps[i] = (valueForStep(i, numSteps) > thresh);
		}
		
		// don't go over the limit
		if(PatternUtil.numStepsActive(steps) > maxSteps) {
			PatternUtil.limitToMaxSteps(steps, maxSteps);
		}
		
		// make sure we have at least one
		PatternUtil.ensureOneStepActive(steps);
	}
	
	@Override
	public float valueForStep(float progress, float numSteps) {
		float noiseStep = progress * noiseSpeed;
		return P.p.noise(noiseStart + noiseStep);
	}
	
	@Override
	public void update() {
	}
}
