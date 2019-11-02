package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class AlphaStepFilter
extends BaseFragmentShader {

	public static AlphaStepFilter instance;
	
	public AlphaStepFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/alpha-step.glsl");
		setAlphaStep(-1f/255f);
	}
	
	public static AlphaStepFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new AlphaStepFilter(p);
		return instance;
	}
	
	public void setAlphaStep(float alphaStep) {
		shader.set("alphaStep", alphaStep);
	}
	
}
