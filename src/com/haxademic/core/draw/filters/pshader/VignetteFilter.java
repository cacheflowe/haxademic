package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class VignetteFilter
extends BaseFragmentShader {

	public static VignetteFilter instance;
	
	public VignetteFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/vignette.glsl");
		setDarkness(0.85f);
		setSpread(0.15f);
	}
	
	public static VignetteFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new VignetteFilter(p);
		return instance;
	}
	
	public void setDarkness(float darkness) {
		shader.set("darkness", darkness);
	}
	
	public void setSpread(float spread) {
		shader.set("spread", spread);
	}
	
}