package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class SaturateHSVFilter
extends BaseFilter {

	public static SaturateHSVFilter instance;
	
	public SaturateHSVFilter(PApplet p) {
		super(p, "shaders/filters/saturate-hsv.glsl");
		setSaturation(0.5f);
	}
	
	public static SaturateHSVFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new SaturateHSVFilter(p);
		return instance;
	}
	
	public void setSaturation(float saturation) {
		shader.set("saturation", saturation);
	}
	
}
