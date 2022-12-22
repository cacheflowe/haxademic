package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class DenoiseSmartFilter
extends BaseFragmentShader {

	public static DenoiseSmartFilter instance;
	
	public DenoiseSmartFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/denoise-smart.glsl");
		setSigma(5f);
		setKSigma(2f);
		setThreshold(0.1f);
	}
	
	public static DenoiseSmartFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new DenoiseSmartFilter(p);
		return instance;
	}
	
	public void setSigma(float uSigma) {
		shader.set("uSigma", uSigma);
	}
	
	public void setKSigma(float uKSigma) {
		shader.set("uKSigma", uKSigma);
	}
	
	public void setThreshold(float uThreshold) {
		shader.set("uThreshold", uThreshold);
	}
	
}
