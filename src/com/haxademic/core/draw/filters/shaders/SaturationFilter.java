package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class SaturationFilter
extends BaseFilter {

	public static SaturationFilter instance;
	
	public SaturationFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/saturation.glsl");
		setSaturation(1f);
	}
	
	public static SaturationFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new SaturationFilter(p);
		return instance;
	}
	
	public void setSaturation(float saturation) {
		shader.set("saturation", saturation);
	}
	
}
