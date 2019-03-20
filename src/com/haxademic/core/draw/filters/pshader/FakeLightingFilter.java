package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class FakeLightingFilter
extends BaseFragmentShader {

	public static FakeLightingFilter instance;
	
	public FakeLightingFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/fake-lighting.glsl");
		setAmbient(4.0f);
		setGradAmp(1.0f);
		setGradBlur(1.0f);
		setSpecAmp(1.5f);
		setDiffDark(0.5f);
	}
	
	public static FakeLightingFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new FakeLightingFilter(p);
		return instance;
	}
	
	public void setAmbient(float ambient) {
		shader.set("ambient", ambient);
	}

	public void setGradAmp(float gradAmp) {
		shader.set("gradAmp", gradAmp);
	}
	
	public void setGradBlur(float gradBlur) {
		shader.set("gradBlur", gradBlur);
	}
	
	public void setSpecAmp(float specAmp) {
		shader.set("specAmp", specAmp);
	}
	
	public void setDiffDark(float diffDark) {
		shader.set("diffDark", diffDark);
	}

}