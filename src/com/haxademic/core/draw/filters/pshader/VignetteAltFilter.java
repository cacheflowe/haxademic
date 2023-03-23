package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class VignetteAltFilter
extends BaseFragmentShader {

	public static VignetteAltFilter instance;
	
	public VignetteAltFilter() {
		super("haxademic/shaders/filters/vignette-alt.glsl");
		setDarkness(0.85f);
		setSpread(0.15f);
	}
	
	public static VignetteAltFilter instance() {
		if(instance != null) return instance;
		instance = new VignetteAltFilter();
		return instance;
	}
	
	public void setDarkness(float darkness) {
		shader.set("darkness", darkness);
	}
	
	public void setSpread(float spread) {
		shader.set("spread", spread);
	}
	
}