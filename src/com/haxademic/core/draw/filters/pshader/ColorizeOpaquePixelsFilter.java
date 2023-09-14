package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class ColorizeOpaquePixelsFilter
extends BaseFragmentShader {

	public static ColorizeOpaquePixelsFilter instance;
	
	public ColorizeOpaquePixelsFilter() {
		super("haxademic/shaders/filters/opaque-pixels-to-color.glsl");
		setColor(0, 0, 0, 1);
	}
	
	public static ColorizeOpaquePixelsFilter instance() {
		if(instance != null) return instance;
		instance = new ColorizeOpaquePixelsFilter();
		return instance;
	}
	
	public void setColor(float r, float g, float b, float a) {
		shader.set("color", r, g, b, a);
	}
	
}
