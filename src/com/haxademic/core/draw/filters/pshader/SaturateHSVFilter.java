package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class SaturateHSVFilter
extends BaseFragmentShader {

	public static SaturateHSVFilter instance;
	
	public SaturateHSVFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/saturate-hsv.glsl");
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
