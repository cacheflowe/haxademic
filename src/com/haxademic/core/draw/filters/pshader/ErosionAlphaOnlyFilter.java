package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class ErosionAlphaOnlyFilter
extends BaseFragmentShader {

	public static ErosionAlphaOnlyFilter instance;
	
	public ErosionAlphaOnlyFilter() {
		super("haxademic/shaders/filters/erosion-alpha-only.glsl");
	}
	
	public static ErosionAlphaOnlyFilter instance() {
		if(instance != null) return instance;
		instance = new ErosionAlphaOnlyFilter();
		return instance;
	}

}
