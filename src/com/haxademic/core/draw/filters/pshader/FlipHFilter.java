package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class FlipHFilter
extends BaseFragmentShader {

	public static FlipHFilter instance;
	
	public FlipHFilter() {
		super("haxademic/shaders/filters/flip-h.glsl");
	}
	
	public static FlipHFilter instance() {
		if(instance != null) return instance;
		instance = new FlipHFilter();
		return instance;
	}

}
