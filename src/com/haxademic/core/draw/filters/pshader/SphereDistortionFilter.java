package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class SphereDistortionFilter
extends BaseFragmentShader {

	public static SphereDistortionFilter instance;
	
	public SphereDistortionFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/sphere-distortion.glsl");
		setAmplitude(1f);
	}
	
	public static SphereDistortionFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new SphereDistortionFilter(p);
		return instance;
	}

	public void setAmplitude(float amplitude) {
		shader.set("amplitude", amplitude);
	}

}
