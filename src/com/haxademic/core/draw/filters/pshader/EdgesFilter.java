package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class EdgesFilter
extends BaseFragmentShader {

	public static EdgesFilter instance;
	
	public EdgesFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/edges.glsl");
	}
	
	public static EdgesFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new EdgesFilter(p);
		return instance;
	}

}
