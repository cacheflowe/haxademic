package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class PixelateHexFilter
extends BaseFragmentShader {

	public static PixelateHexFilter instance;
	
	public PixelateHexFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/pixelate-hex.glsl");
		setDivider(20f);
	}
	
	public static PixelateHexFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new PixelateHexFilter(p);
		return instance;
	}
	
	public void setDivider(float divider) {
		shader.set("divider", divider);
	}
	
}
