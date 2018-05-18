package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class FlipHFilter
extends BaseFilter {

	public static FlipHFilter instance;
	
	public FlipHFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/flip-h.glsl");
	}
	
	public static FlipHFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new FlipHFilter(p);
		return instance;
	}

}
