package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class SaturationFilter
extends BaseFragmentShader {

	public static SaturationFilter instance;
	
	public SaturationFilter() {
		super("haxademic/shaders/filters/saturation.glsl");
		setSaturation(1f);
	}
	
	public static SaturationFilter instance() {
		if(instance != null) return instance;
		instance = new SaturationFilter();
		return instance;
	}
	
	public void setSaturation(float saturation) {
		shader.set("saturation", saturation);
	}
	
}
