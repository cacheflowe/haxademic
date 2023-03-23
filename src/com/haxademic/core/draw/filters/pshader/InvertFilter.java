package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class InvertFilter
extends BaseFragmentShader {

	public static InvertFilter instance;
	
	public InvertFilter() {
		super("haxademic/shaders/filters/invert.glsl");
	}
	
	public static InvertFilter instance() {
		if(instance != null) return instance;
		instance = new InvertFilter();
		return instance;
	}

}
