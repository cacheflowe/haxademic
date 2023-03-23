package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class SphereDistortionFilter
extends BaseFragmentShader {

	public static SphereDistortionFilter instance;
	
	public SphereDistortionFilter() {
		super("haxademic/shaders/filters/sphere-distortion.glsl");
		setAmplitude(1f);
	}
	
	public static SphereDistortionFilter instance() {
		if(instance != null) return instance;
		instance = new SphereDistortionFilter();
		return instance;
	}

	public void setAmplitude(float amplitude) {
		shader.set("amplitude", amplitude);
	}

}
