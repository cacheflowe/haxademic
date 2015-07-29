package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class BlurVFilter
extends BaseFilter {

	public static BlurVFilter instance;
	
	public BlurVFilter(PApplet p) {
		super(p, "shaders/filters/blur-vertical.glsl");
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
	
}
