package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class BadTVGlitchFilter
extends BaseFilter {

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