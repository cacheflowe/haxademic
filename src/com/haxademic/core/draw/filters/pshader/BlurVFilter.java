package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class BlurVFilter
extends BaseFragmentShader {

	public static BlurVFilter instance;
	
	public BlurVFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/blur-vertical.glsl");
		setBlur(0.001f);
	}
	
	public static BlurVFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BlurVFilter(p);
		return instance;
	}
	
	public void setBlur(float blur) {
		shader.set("v", blur);
	}
	
	public void setBlurByPercent(float blurPercent, float imageWidth) {
		shader.set("v", blurPercent * (1f / imageWidth));
	}
	
}
