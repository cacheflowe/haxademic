package com.haxademic.core.data.patterns;

import com.haxademic.core.math.MathUtil;

public class PatternUtil {

	// COUNT
	
	public static int numStepsActive(boolean[] steps) {
		int activeCount = 0;
		for (int i = 0; i < steps.length; i++) {
			if(steps[i] == true) activeCount++;
		}
		return activeCount;
	}

	public static int findRandomActiveIndex(boolean[] steps) {
		int activeIndex = -1;
		int attempts = 0;
		while(activeIndex == -1 && attempts < 100) {
			int randomIndex = MathUtil.randRange(0, steps.length - 1);
			if(steps[randomIndex] == true) activeIndex = randomIndex;
			attempts++;
		}
		return activeIndex;
	}
	
	public static int findRandomInctiveIndex(boolean[] steps) {
		int inactiveIndex = -1;
		int attempts = 0;
		while(inactiveIndex == -1 && attempts < 100) {
			int randomIndex = MathUtil.randRange(0, steps.length - 1);
			if(steps[randomIndex] == false) inactiveIndex = randomIndex;
			attempts++;
		}
		return inactiveIndex;
	}
	
	// LIMIT
	
	public static void ensureOneStepActive(boolean[] steps) {
		if(PatternUtil.numStepsActive(steps) == 0) {
			steps[MathUtil.randRange(0, steps.length - 1)] = true;
		}
	}
	
	public static void limitToMaxSteps(boolean[] steps, int maxSteps) {
		while(PatternUtil.numStepsActive(steps) > maxSteps) {
			int randomIndex = MathUtil.randRange(0, steps.length - 1);
			if(steps[randomIndex] == true) steps[randomIndex] = false;
		}
	}
	
	// MORPH
	
	public static void nudgePatternForward(boolean[] steps) {
		boolean lastStep = steps[steps.length - 1];
		for (int i = steps.length - 1; i > 0; i--) {
			steps[i] = steps[i-1];
		}
		steps[0] = lastStep;
	}
	
	public static void morphPattern(boolean[] steps) {
		int randomActiveIndex = PatternUtil.findRandomActiveIndex(steps);
		int randomInctiveIndex = PatternUtil.findRandomInctiveIndex(steps);
		if(randomActiveIndex >= 0 && randomInctiveIndex >= 0) {
			steps[randomActiveIndex] = false;
			steps[randomInctiveIndex] = true;
		}
	}
	

}
