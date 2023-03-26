package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class BrightnessFilter
extends BaseFragmentShader {

	public static BrightnessFilter instance;
	
	public BrightnessFilter() {
		super("haxademic/shaders/filters/brightness.glsl");
		setBrightness(1f);
	}
	
	public static BrightnessFilter instance() {
		if(instance != null) return instance;
		instance = new BrightnessFilter();
		return instance;
	}
	
	public void setBrightness(float brightness) {
		shader.set("brightness", brightness);
	}
	
}
