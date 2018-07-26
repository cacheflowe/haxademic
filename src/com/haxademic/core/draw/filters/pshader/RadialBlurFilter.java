package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class RadialBlurFilter
extends BaseFragmentShader {

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