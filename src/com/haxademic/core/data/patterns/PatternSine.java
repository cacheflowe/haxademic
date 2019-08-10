package com.haxademic.core.data.patterns;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class PatternSine
implements ISequencerPattern {

	protected float thresh = 0.6f; 
	protected int maxSteps = 8; 
	protected float sinSpeed = 0.1f;
	protected float sinStart = 0;

	public PatternSine() {
		this(0.6f, 8);
	}
	
	public PatternSine(float thresh) {
		this(thresh, 8);
	}

	public PatternSine(int maxSteps) {
		this(0.6f, maxSteps);
	}
	
	public PatternSine(float thresh, int maxSteps) {
		this.thresh = thresh;
		this.maxSteps = maxSteps;
	}


	@Override
	public void newPattern(boolean[] steps) {
		int numSteps = steps.length;

		// new sin() props
		if(MathUtil.randBoolean()) {
			sinSpeed = MathUtil.randRangeDecimal(1f, 5f);
		} else {
			sinSpeed = MathUtil.randRangeDecimal(7f, 12f);
		}
		sinStart = MathUtil.randRangeDecimal(0, P.TWO_PI);
		
		// mouse control debug
		/**
		sinSpeed = P.p.mousePercentX() * 20f;
		sinStart = P.p.mousePercentY() * P.TWO_PI;
		P.p.debugView.setValue("sinSpeed", sinSpeed);
		P.p.debugView.setValue("sinStart", sinStart);
		*/
		
		// apply sin() to steps
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
		return 0.5f + 0.5f * P.sin(sinStart + sinSpeed * progress);
	}
	
	@Override
	public void update() {
	}
}
