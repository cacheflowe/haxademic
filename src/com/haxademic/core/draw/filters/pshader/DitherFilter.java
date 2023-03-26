package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class DitherFilter
extends BaseFragmentShader {

	public static DitherFilter instance;
	
	public DitherFilter() {
		super("haxademic/shaders/filters/dither.glsl");
		setDitherMode8x8();
	}
	
	public static DitherFilter instance() {
		if(instance != null) return instance;
		instance = new DitherFilter();
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
