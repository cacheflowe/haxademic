package com.haxademic.core.data.patterns;

public interface ISequencerPattern {

	public void newPattern(boolean[] slots);
	public float valueForStep(float progress, float numSteps);
	public void update();
	
}
