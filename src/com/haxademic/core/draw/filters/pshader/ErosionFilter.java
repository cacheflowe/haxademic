package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class ErosionFilter
extends BaseFragmentShader {

	public static ErosionFilter instance;
	
	public ErosionFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/erosion.glsl");
	}
	
	public static ErosionFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ErosionFilter(p);
		return instance;
	}

}
