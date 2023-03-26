package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class BrightnessRemapFilter
extends BaseFragmentShader {

	public static BrightnessRemapFilter instance;
	
	public BrightnessRemapFilter() {
		super("haxademic/shaders/filters/brightness-remap.glsl");
		setLow(0f);
		setHigh(1f);
	}
	
	public static BrightnessRemapFilter instance() {
		if(instance != null) return instance;
		instance = new BrightnessRemapFilter();
		return instance;
	}
	
	public void setLow(float low) {
		shader.set("low", low);
	}
	
	public void setHigh(float high) {
		shader.set("high", high);
	}
	
}
