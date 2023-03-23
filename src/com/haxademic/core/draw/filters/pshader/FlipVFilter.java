package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class FlipVFilter
extends BaseFragmentShader {

	public static FlipVFilter instance;
	
	public FlipVFilter() {
		super("haxademic/shaders/filters/flip-v.glsl");
	}
	
	public static FlipVFilter instance() {
		if(instance != null) return instance;
		instance = new FlipVFilter();
		return instance;
	}

}
