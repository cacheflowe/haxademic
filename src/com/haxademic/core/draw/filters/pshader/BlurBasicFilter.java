package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class BlurBasicFilter
extends BaseFragmentShader {

	public static BlurBasicFilter instance;
	
	public BlurBasicFilter() {
		super("haxademic/shaders/filters/blur-basic.glsl");
	}
	
	public static BlurBasicFilter instance() {
		if(instance != null) return instance;
		instance = new BlurBasicFilter();
		return instance;
	}
}
