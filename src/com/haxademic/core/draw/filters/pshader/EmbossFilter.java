package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class EmbossFilter
extends BaseFragmentShader {

	public static EmbossFilter instance;
	
	public EmbossFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/emboss.glsl");
	}
	
	public static EmbossFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new EmbossFilter(p);
		return instance;
	}

}
