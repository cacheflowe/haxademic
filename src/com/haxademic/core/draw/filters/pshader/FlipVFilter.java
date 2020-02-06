package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class FlipVFilter
extends BaseFragmentShader {

	public static FlipVFilter instance;
	
	public FlipVFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/flip-v.glsl");
	}
	
	public static FlipVFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new FlipVFilter(p);
		return instance;
	}

}
