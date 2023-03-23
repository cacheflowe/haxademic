package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class BlurHFilter
extends BaseFragmentShader {

	public static BlurHFilter instance;
	
	public BlurHFilter() {
		super("haxademic/shaders/filters/blur-horizontal.glsl");
		setBlur(0f);
	}
	
	public static BlurHFilter instance() {
		if(instance != null) return instance;
		instance = new BlurHFilter();
		return instance;
	}
	
	public void setBlur(float blur) {
		shader.set("h", blur);
	}
	
	public void setBlurByPercent(float blurPercent, float imageWidth) {
		shader.set("h", blurPercent * (1f / imageWidth));
	}	
	
}
