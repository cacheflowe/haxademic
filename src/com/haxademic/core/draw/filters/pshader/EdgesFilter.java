package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class EdgesFilter
extends BaseFragmentShader {

	public static EdgesFilter instance;
	
	public EdgesFilter() {
		super("haxademic/shaders/filters/edges.glsl");
	}
	
	public static EdgesFilter instance() {
		if(instance != null) return instance;
		instance = new EdgesFilter();
		return instance;
	}

}
