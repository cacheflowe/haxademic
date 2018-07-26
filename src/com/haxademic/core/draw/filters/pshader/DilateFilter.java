package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class DilateFilter
extends BaseFragmentShader {

	public static DilateFilter instance;
	
	public DilateFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/dilate.glsl");
	}
	
	public static DilateFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new DilateFilter(p);
		return instance;
	}

}
