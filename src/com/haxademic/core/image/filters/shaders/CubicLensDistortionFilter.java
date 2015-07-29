package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class CubicLensDistortionFilter
extends BaseFilter {

	public static CubicLensDistortionFilter instance;
	
	public CubicLensDistortionFilter(PApplet p) {
		super(p, "shaders/filters/cubic-lens-distortion.glsl");
	}
	
	public static CubicLensDistortionFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new CubicLensDistortionFilter(p);
		return instance;
	}
	
}
