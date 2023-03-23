package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class BlurVFilter
extends BaseFragmentShader {

	public static BlurVFilter instance;
	
	public BlurVFilter() {
		super("haxademic/shaders/filters/blur-vertical.glsl");
		setBlur(0f);
	}
	
	public static BlurVFilter instance() {
		if(instance != null) return instance;
		instance = new BlurVFilter();
		return instance;
	}
	
	public void setBlur(float blur) {
		shader.set("v", blur);
	}
	
	public void setBlurByPercent(float blurPercent, float imageHeight) {
		shader.set("v", blurPercent * (1f / imageHeight));
	}
	
}
