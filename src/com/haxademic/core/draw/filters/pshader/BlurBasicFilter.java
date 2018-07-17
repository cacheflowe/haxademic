package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class BlurBasicFilter
extends BaseFragmentShader {

	public static BlurBasicFilter instance;
	
	public BlurBasicFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/blur-basic.glsl");
	}
	
	public static BlurBasicFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BlurBasicFilter(p);
		return instance;
	}
}
