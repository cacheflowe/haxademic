package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class VignetteFilter
extends BaseFragmentShader {

	public static VignetteFilter instance;
	
	public VignetteFilter() {
		super("haxademic/shaders/filters/vignette.glsl");
		setDarkness(0.85f);
		setSpread(0.15f);
	}
	
	public static VignetteFilter instance() {
		if(instance != null) return instance;
		instance = new VignetteFilter();
		return instance;
	}
	
	public VignetteFilter setDarkness(float darkness) {
		shader.set("darkness", darkness);
		return instance;
	}
	
	public VignetteFilter setSpread(float spread) {
		shader.set("spread", spread);
		return instance;
	}
	
}