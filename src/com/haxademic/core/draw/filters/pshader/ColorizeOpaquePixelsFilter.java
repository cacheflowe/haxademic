package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class ColorizeOpaquePixelsFilter
extends BaseFragmentShader {

	public static ColorizeOpaquePixelsFilter instance;
	
	public ColorizeOpaquePixelsFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/opaque-pixels-to-color.glsl");
		setColor(0f, 0f, 0f);
	}
	
	public static ColorizeOpaquePixelsFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ColorizeOpaquePixelsFilter(p);
		return instance;
	}
	
	public void setColor(float r, float g, float b) {
		shader.set("color", r, g, b);
	}
	
}
