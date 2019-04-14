package com.haxademic.core.data.patterns;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class PatternTechno
implements ISequencerPattern {

	public PatternTechno() {
	}
	
	@Override
	public void newPattern(boolean[] steps) {
		int divisor = MathUtil.randBoolean(P.p) ? 4 : 2;
		divisor = 4;
		for (int i = 0; i < steps.length; i++) {
			steps[i] = (i % divisor == 0);
		}
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
