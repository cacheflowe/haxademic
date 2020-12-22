package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class ToneMappingFilter
extends BaseFragmentShader {

	public static ToneMappingFilter instance;
	
	public ToneMappingFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/tone-mapping.glsl");
		setMode(1);
		setGamma(2.2f);
		setCrossfade(1f);
	}
	
	public static ToneMappingFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ToneMappingFilter(p);
		return instance;
	}
	
	public void setMode(int mode) {
		shader.set("mode", mode);
	}
	
	public void setGamma(float gamma) {
		shader.set("gamma", gamma);
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
