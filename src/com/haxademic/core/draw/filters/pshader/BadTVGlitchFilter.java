package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class BadTVGlitchFilter
extends BaseFragmentShader {

	public static BadTVGlitchFilter instance;
	
	public BadTVGlitchFilter() {
		super("haxademic/shaders/filters/badtv2.glsl");
	}
	
	public static BadTVGlitchFilter instance() {
		if(instance != null) return instance;
		instance = new BadTVGlitchFilter();
		return instance;
	}
	
}