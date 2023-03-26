package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class BrightnessClampFilter
extends BaseFragmentShader {

	public static BrightnessClampFilter instance;
	
	public BrightnessClampFilter() {
		super("haxademic/shaders/filters/brightness-clamp.glsl");
		setLow(0f);
		setHigh(1f);
	}
	
	public static BrightnessClampFilter instance() {
		if(instance != null) return instance;
		instance = new BrightnessClampFilter();
		return instance;
	}
	
	public void setLow(float low) {
		shader.set("low", low);
	}
	
	public void setHigh(float high) {
		shader.set("high", high);
	}
	
}
