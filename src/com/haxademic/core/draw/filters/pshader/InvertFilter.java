package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class InvertFilter
extends BaseFragmentShader {

	public static InvertFilter instance;
	
	public InvertFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/invert.glsl");
	}
	
	public static InvertFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new InvertFilter(p);
		return instance;
	}

}
