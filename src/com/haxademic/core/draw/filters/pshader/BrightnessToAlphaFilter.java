package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class BrightnessToAlphaFilter
extends BaseFragmentShader {

	public static BrightnessToAlphaFilter instance;
	
	public BrightnessToAlphaFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/brightness-to-alpha.glsl");
		setFlip(false);
	}
	
	public static BrightnessToAlphaFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BrightnessToAlphaFilter(p);
		return instance;
	}
	
	public void setFlip(boolean flip) {
		shader.set("flip", (flip == true) ? 1 : 0);
	}
		
}
