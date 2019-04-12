package com.haxademic.core.data.patterns;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class PatternSingleStep
implements ISequencerPattern {

	protected int curIndex = 0;
	
	public PatternSingleStep() {
		
	}

	@Override
	public void newPattern(boolean[] steps) {
		// clear previous set
		for (int i = 0; i < steps.length; i++) {
			steps[i] = false;
		}
		// pick random step on
		curIndex = MathUtil.randRange(0, steps.length - 1);
		steps[curIndex] = true;
	}
	
	@Override
	public float valueForStep(float progress, float numSteps) {
		return (P.floor(progress) == curIndex) ? 1f : 0f;
	}
	
	@Override
	public void update() {
	}
}
