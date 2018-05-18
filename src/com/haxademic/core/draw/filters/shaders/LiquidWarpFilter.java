package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class LiquidWarpFilter
extends BaseFilter {

	public static LiquidWarpFilter instance;
	
	public LiquidWarpFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/liquid-warp.glsl");
		setAmplitude(0.02f);
		setFrequency(6.0f);
	}
	
	public static LiquidWarpFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new LiquidWarpFilter(p);
		return instance;
	}
	
	public void setAmplitude(float amplitude) {
		shader.set("amplitude", amplitude);
	}
	
	public void setFrequency(float frequency) {
		shader.set("frequency", frequency);
	}
	
}