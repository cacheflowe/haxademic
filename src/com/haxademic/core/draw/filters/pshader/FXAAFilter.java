package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class FXAAFilter
extends BaseFragmentShader {

	public static FXAAFilter instance;
	
	public FXAAFilter() {
		super("haxademic/shaders/filters/fxaa.glsl");
	}
	
	public static FXAAFilter instance() {
		if(instance != null) return instance;
		instance = new FXAAFilter();
		return instance;
	}

}
