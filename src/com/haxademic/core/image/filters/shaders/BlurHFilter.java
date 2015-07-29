package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class BlurHFilter
extends BaseFilter {

	public static BlurHFilter instance;
	
	public BlurHFilter(PApplet p) {
		super(p, "shaders/filters/blur-horizontal.glsl");
		setBlur(0.001f);
	}
	
	public static BlurHFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BlurHFilter(p);
		return instance;
	}
	
	public void setBlur(float blur) {
		shader.set("h", blur);
	}
	
}
