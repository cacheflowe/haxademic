package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class BrightnessStepFilter
extends BaseFragmentShader {

	public static BrightnessStepFilter instance;
	
	public BrightnessStepFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/brightness-step.glsl");
		setBrightnessStep(-1f/255f);
	}
	
	public static BrightnessStepFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BrightnessStepFilter(p);
		return instance;
	}
	
	public void setBrightnessStep(float brightStep) {
		shader.set("brightStep", brightStep);
	}
	
}
