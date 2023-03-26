package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class ErosionFilter
extends BaseFragmentShader {

	public static ErosionFilter instance;
	
	public ErosionFilter() {
		super("haxademic/shaders/filters/erosion.glsl");
	}
	
	public static ErosionFilter instance() {
		if(instance != null) return instance;
		instance = new ErosionFilter();
		return instance;
	}

}
