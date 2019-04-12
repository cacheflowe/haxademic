package com.haxademic.core.data.patterns;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;

public class PatternInterval
implements ISequencerPattern {

	protected int startIndex = 0;
	protected int interval = 2;
	protected int intervalMin = 2;
	protected int intervalMax = 8;
	
	public PatternInterval() {
		this(2, 8);
	}

	public PatternInterval(int intervalMin, int intervalMax) {
		this.intervalMin = intervalMin;
		this.intervalMax = intervalMax;
	}
	
	@Override
	public void newPattern(boolean[] steps) {
		// new interval props
		int numSteps = steps.length;
		startIndex = MathUtil.randRange(0, numSteps - 1);
		interval = MathUtil.randRange(intervalMin, intervalMax);
		if(interval % 2 == 1 && MathUtil.randBoolean(P.p)) interval += 1; // more often go for even spacing if an odd number was randomized
		
		// mouse control debug
		/**
		startIndex = P.floor(P.p.mousePercentX() * numSteps);
		stepsBetween = P.floor(2 + P.p.mousePercentY() * 7);
		P.p.debugView.setValue("startIndex", startIndex);
		P.p.debugView.setValue("stepsBetween", stepsBetween);
		*/
				
		// clear previous set
		for (int i = 0; i < steps.length; i++) steps[i] = false;
		
		// fill in on interval
		int curStep = startIndex;
		while(curStep < startIndex + numSteps - 1) {
			steps[curStep % numSteps] = true;
			curStep += interval;
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
