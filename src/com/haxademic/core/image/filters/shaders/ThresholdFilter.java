package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class ThresholdFilter
extends BaseFilter {

	public static ThresholdFilter instance;
	
	public ThresholdFilter(PApplet p) {
		super(p, "shaders/filters/threshold.glsl");
	}
	
	public static ThresholdFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ThresholdFilter(p);
		return instance;
	}

}
