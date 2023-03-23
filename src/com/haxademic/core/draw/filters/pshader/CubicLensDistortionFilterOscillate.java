package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class CubicLensDistortionFilterOscillate
extends BaseFragmentShader {

	public static CubicLensDistortionFilterOscillate instance;
	
	public CubicLensDistortionFilterOscillate() {
		super("haxademic/shaders/filters/cubic-lens-distortion-oscillate.glsl");
	}
	
	public static CubicLensDistortionFilterOscillate instance() {
		if(instance != null) return instance;
		instance = new CubicLensDistortionFilterOscillate();
		return instance;
	}

}
