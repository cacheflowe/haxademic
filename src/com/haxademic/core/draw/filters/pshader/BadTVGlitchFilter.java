package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class BadTVGlitchFilter
extends BaseFragmentShader {

	public static BadTVGlitchFilter instance;
	
	public BadTVGlitchFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/badtv2.glsl");
	}
	
	public static BadTVGlitchFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BadTVGlitchFilter(p);
		return instance;
	}
	
}