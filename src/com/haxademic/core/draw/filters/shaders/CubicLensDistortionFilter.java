package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class CubicLensDistortionFilter
extends BaseFilter {

	public static CubicLensDistortionFilter instance;
	
	public CubicLensDistortionFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/cubic-lens-distortion.glsl");
		setAmplitude(0);
	}
	
	public static CubicLensDistortionFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new CubicLensDistortionFilter(p);
		return instance;
	}
	
	public void setAmplitude(float amp) {
		shader.set("amp", amp);
	}

	public void setSeparation(float separation) {
		shader.set("separation", separation);
	}
	
}
