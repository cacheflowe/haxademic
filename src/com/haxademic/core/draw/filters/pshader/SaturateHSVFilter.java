package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class SaturateHSVFilter
extends BaseFragmentShader {

	public static SaturateHSVFilter instance;
	
	public SaturateHSVFilter() {
		super("haxademic/shaders/filters/saturate-hsv.glsl");
		setSaturation(0.5f);
	}
	
	public static SaturateHSVFilter instance() {
		if(instance != null) return instance;
		instance = new SaturateHSVFilter();
		return instance;
	}
	
	public void setSaturation(float saturation) {
		shader.set("saturation", saturation);
	}
	
}
