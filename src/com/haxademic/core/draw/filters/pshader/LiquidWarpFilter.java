package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class LiquidWarpFilter
extends BaseFragmentShader {

	public static LiquidWarpFilter instance;
	
	public LiquidWarpFilter() {
		super("haxademic/shaders/filters/liquid-warp.glsl");
		setAmplitude(0.02f);
		setFrequency(6.0f);
	}
	
	public static LiquidWarpFilter instance() {
		if(instance != null) return instance;
		instance = new LiquidWarpFilter();
		return instance;
	}
	
	public void setAmplitude(float amplitude) {
		shader.set("amplitude", amplitude);
	}
	
	public void setFrequency(float frequency) {
		shader.set("frequency", frequency);
	}
	
}