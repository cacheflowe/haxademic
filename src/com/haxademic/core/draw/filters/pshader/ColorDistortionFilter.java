package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class ColorDistortionFilter
extends BaseFragmentShader {

	public static ColorDistortionFilter instance;
	
	public ColorDistortionFilter() {
		super("haxademic/shaders/filters/color-distortion.glsl");
		setAmplitude(1f);
	}
	
	public static ColorDistortionFilter instance() {
		if(instance != null) return instance;
		instance = new ColorDistortionFilter();
		return instance;
	}
	
	public void setAmplitude(float amplitude) {
		shader.set("amplitude", amplitude);
	}
	
}
