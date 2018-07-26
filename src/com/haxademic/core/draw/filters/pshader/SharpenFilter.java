package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class SharpenFilter
extends BaseFragmentShader {

	public static SharpenFilter instance;
	
	public SharpenFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/sharpen.glsl");
		setSharpness(1f);
	}
	
	public static SharpenFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new SharpenFilter(p);
		return instance;
	}

	public void setSharpness(float sharpness) {
		shader.set("sharpness", sharpness);
	}
	
}
