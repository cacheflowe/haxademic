package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class BrightnessClampFilter
extends BaseFragmentShader {

	public static BrightnessClampFilter instance;
	
	public BrightnessClampFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/brightness-clamp.glsl");
		setLow(0f);
		setHigh(1f);
	}
	
	public static BrightnessClampFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BrightnessClampFilter(p);
		return instance;
	}
	
	public void setLow(float low) {
		shader.set("low", low);
	}
	
	public void setHigh(float high) {
		shader.set("high", high);
	}
	
}
