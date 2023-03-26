package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class BrightnessStepFilter
extends BaseFragmentShader {

	public static BrightnessStepFilter instance;
	
	public BrightnessStepFilter() {
		super("haxademic/shaders/filters/brightness-step.glsl");
		setBrightnessStep(-1f/255f);
	}
	
	public static BrightnessStepFilter instance() {
		if(instance != null) return instance;
		instance = new BrightnessStepFilter();
		return instance;
	}
	
	public void setBrightnessStep(float brightStep) {
		shader.set("brightStep", brightStep);
	}
	
}
