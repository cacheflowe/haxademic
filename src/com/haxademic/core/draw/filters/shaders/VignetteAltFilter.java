package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class VignetteAltFilter
extends BaseFilter {

	public static VignetteAltFilter instance;
	
	public VignetteAltFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/vignette-alt.glsl");
		setDarkness(0.85f);
		setSpread(0.15f);
	}
	
	public static VignetteAltFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new VignetteAltFilter(p);
		return instance;
	}
	
	public void setDarkness(float darkness) {
		shader.set("darkness", darkness);
	}
	
	public void setSpread(float spread) {
		shader.set("spread", spread);
	}
	
}