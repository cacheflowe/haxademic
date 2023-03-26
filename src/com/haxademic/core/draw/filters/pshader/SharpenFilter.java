package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class SharpenFilter
extends BaseFragmentShader {

	public static SharpenFilter instance;
	
	public SharpenFilter() {
		super("haxademic/shaders/filters/sharpen.glsl");
		setSharpness(1f);
	}
	
	public static SharpenFilter instance() {
		if(instance != null) return instance;
		instance = new SharpenFilter();
		return instance;
	}

	public void setSharpness(float sharpness) {
		shader.set("sharpness", sharpness);
	}
	
}
