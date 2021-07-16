package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class DitherFilter
extends BaseFragmentShader {

	public static DitherFilter instance;
	
	public DitherFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/dither.glsl");
		setDitherMode8x8();
	}
	
	public static DitherFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new DitherFilter(p);
		return instance;
	}
	
	public void setDitherMode2x2() {
		shader.set("ditherMode", 0);
	}
	
	public void setDitherMode4x4() {
		shader.set("ditherMode", 1);
	}
	
	public void setDitherMode8x8() {
		shader.set("ditherMode", 2);
	}
	
}
