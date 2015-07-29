package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class RadialBlurFilter
extends BaseFilter {

	public static RadialBlurFilter instance;
	
	public RadialBlurFilter(PApplet p) {
		super(p, "shaders/filters/radial-blur-iq.glsl");
	}
	
	public static RadialBlurFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new RadialBlurFilter(p);
		return instance;
	}
	
}