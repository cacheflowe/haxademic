package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class DilateFilter
extends BaseFragmentShader {

	public static DilateFilter instance;
	
	public DilateFilter() {
		super("haxademic/shaders/filters/dilate.glsl");
	}
	
	public static DilateFilter instance() {
		if(instance != null) return instance;
		instance = new DilateFilter();
		return instance;
	}

}
