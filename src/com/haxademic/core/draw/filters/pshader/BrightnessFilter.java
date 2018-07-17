package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class BrightnessFilter
extends BaseFragmentShader {

	public static BrightnessFilter instance;
	
	public BrightnessFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/brightness.glsl");
		setBrightness(1f);
	}
	
	public static BrightnessFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BrightnessFilter(p);
		return instance;
	}
	
	public void setBrightness(float brightness) {
		shader.set("brightness", brightness);
	}
	
}
