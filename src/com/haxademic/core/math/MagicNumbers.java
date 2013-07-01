package com.haxademic.core.math;

import processing.core.PApplet;

public class MagicNumbers {
	/**
	 * A great way to traverse circular trigonometry.
	 */
	static final float GOLDEN_RATIO = ((float)Math.sqrt(5f)+1f)/2f - 1f;
	
	/**
	 * Works with Golden ratio for a good time.
	 */
	static final float GOLDEN_ANGLE = GOLDEN_RATIO * PApplet.TWO_PI;
	
	/**
	 * K
	 */
	static final float SIERPINKSI = 2.584981759579253217f;
	
	/**
	 * Natural log()
	 */
	static final float E = 2.71828182845904523536028747135266249f;
	
	/**
	 * "Golden ratio for exponentials".
	 */
	static final float OMEGA = 0.56714329040978387299996866221035555f;
	
	/**
	 * A naturally occurring transcendental number that exhibits continued fraction expansion.
	 */
	static final float CAHEN = 0.6434105463f;
}
