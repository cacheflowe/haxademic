package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class ThresholdFilter
extends BaseFilter {

	public static ThresholdFilter instance;
	
	public ThresholdFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/threshold.glsl");
		setCutoff(0.5f);
	}
	
	public static ThresholdFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ThresholdFilter(p);
		return instance;
	}

	public void setCutoff(float cutoff) {
		shader.set("cutoff", cutoff);
	}
	
}
