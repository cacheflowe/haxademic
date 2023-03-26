package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class CubicLensDistortionFilter
extends BaseFragmentShader {

	public static CubicLensDistortionFilter instance;
	
	public CubicLensDistortionFilter() {
		super("haxademic/shaders/filters/cubic-lens-distortion.glsl");
		setAmplitude(0);
	}
	
	public static CubicLensDistortionFilter instance() {
		if(instance != null) return instance;
		instance = new CubicLensDistortionFilter();
		return instance;
	}
	
	public void setAmplitude(float amp) {
		shader.set("amp", amp);
	}

	public void setSeparation(float separation) {
		shader.set("separation", separation);
	}
	
}
