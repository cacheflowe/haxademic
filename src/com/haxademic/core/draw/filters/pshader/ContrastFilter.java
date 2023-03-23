package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class ContrastFilter
extends BaseFragmentShader {

	public static ContrastFilter instance;
	
	public ContrastFilter() {
		super("haxademic/shaders/filters/contrast.glsl");
		setContrast(1f);
	}
	
	public static ContrastFilter instance() {
		if(instance != null) return instance;
		instance = new ContrastFilter();
		return instance;
	}
	
	public void setContrast(float contrast) {
		shader.set("contrast", contrast);
	}
	
}
