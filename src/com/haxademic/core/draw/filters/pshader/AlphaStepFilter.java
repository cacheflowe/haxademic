package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class AlphaStepFilter
extends BaseFragmentShader {

	public static AlphaStepFilter instance;
	
	public AlphaStepFilter() {
		super("haxademic/shaders/filters/alpha-step.glsl");
		setAlphaStep(-1f/255f);
	}
	
	public static AlphaStepFilter instance() {
		if(instance != null) return instance;
		instance = new AlphaStepFilter();
		return instance;
	}
	
	public void setAlphaStep(float alphaStep) {
		shader.set("alphaStep", alphaStep);
	}
	
}
