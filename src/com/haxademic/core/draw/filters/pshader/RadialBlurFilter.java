package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class RadialBlurFilter
extends BaseFragmentShader {

	public static RadialBlurFilter instance;
	
	public RadialBlurFilter() {
		super("haxademic/shaders/filters/radial-blur-iq.glsl");
	}
	
	public static RadialBlurFilter instance() {
		if(instance != null) return instance;
		instance = new RadialBlurFilter();
		return instance;
	}
	
}