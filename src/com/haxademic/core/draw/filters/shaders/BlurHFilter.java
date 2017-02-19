package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class BlurHFilter
extends BaseFilter {

	public static BlurHFilter instance;
	
	public BlurHFilter(PApplet p) {
		super(p, "shaders/filters/blur-horizontal.glsl");
		setBlur(0f);
	}
	
	public static BlurHFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BlurHFilter(p);
		return instance;
	}
	
	public void setBlur(float blur) {
		shader.set("h", blur);
	}
	
	public void setBlurByPercent(float blurPercent, float imageWidth) {
		shader.set("h", blurPercent * (1f / imageWidth));
	}
	
}
