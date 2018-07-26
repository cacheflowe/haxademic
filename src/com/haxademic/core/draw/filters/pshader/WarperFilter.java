package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class WarperFilter
extends BaseFragmentShader {

	public static WarperFilter instance;
	
	public WarperFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/warper.glsl");
	}
	
	public static WarperFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new WarperFilter(p);
		return instance;
	}
	
}
