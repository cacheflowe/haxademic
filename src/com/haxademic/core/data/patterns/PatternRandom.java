package com.haxademic.core.data.patterns;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class PatternRandom
implements ISequencerPattern {

	protected float weight = 0.3f;
	protected int maxSteps = 8;
	
	public PatternRandom() {
		this(0.3f, 8);
	}
	
	public PatternRandom(float weight) {
		this.weight = weight;
	}

	public PatternRandom(float weight, int maxSteps) {
		this.weight = weight;
		this.maxSteps = maxSteps;
	}
	
	@Override
	public void newPattern(boolean[] steps) {
		// int numSteps = steps.length;

		// set random steps
		for (int i = 0; i < steps.length; i++) {
			steps[i] = MathUtil.randBooleanWeighted(weight);
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
		if(P.floor(progress) > numSteps) return 0;
		return 1;	// this is bogus
	}
	
	@Override
	public void update() {
	}
}
