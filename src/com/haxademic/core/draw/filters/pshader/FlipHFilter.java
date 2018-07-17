package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class FlipHFilter
extends BaseFragmentShader {

	public static FlipHFilter instance;
	
	public FlipHFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/flip-h.glsl");
	}
	
	public static FlipHFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new FlipHFilter(p);
		return instance;
	}

}
