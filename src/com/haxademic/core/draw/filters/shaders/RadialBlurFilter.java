package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class RadialBlurFilter
extends BaseFilter {

	public static RadialBlurFilter instance;
	
	public RadialBlurFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/radial-blur-iq.glsl");
	}
	
	public static RadialBlurFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new RadialBlurFilter(p);
		return instance;
	}
	
}