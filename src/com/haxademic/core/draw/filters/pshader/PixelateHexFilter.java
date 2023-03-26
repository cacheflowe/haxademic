package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class PixelateHexFilter
extends BaseFragmentShader {

	public static PixelateHexFilter instance;
	
	public PixelateHexFilter() {
		super("haxademic/shaders/filters/pixelate-hex.glsl");
		setDivider(20f);
	}
	
	public static PixelateHexFilter instance() {
		if(instance != null) return instance;
		instance = new PixelateHexFilter();
		return instance;
	}
	
	public void setDivider(float divider) {
		shader.set("divider", divider);
	}
	
}
