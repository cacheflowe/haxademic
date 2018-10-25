package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class GlitchPseudoPixelSortingFilter
extends BaseFragmentShader {

	public static GlitchPseudoPixelSortingFilter instance;
	
	public GlitchPseudoPixelSortingFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/glitch-pseudo-pixel-sorting.glsl");
		setThresholdLow(0.1f);
		setThresholdHigh(0.9f);
	}
	
	public static GlitchPseudoPixelSortingFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new GlitchPseudoPixelSortingFilter(p);
		return instance;
	}
	
	public void setThresholdLow(float thresholdLow) {
		shader.set("thresholdLow", thresholdLow);
	}
	
	public void setThresholdHigh(float thresholdHigh) {
		shader.set("thresholdHigh", thresholdHigh);
	}
	
}
