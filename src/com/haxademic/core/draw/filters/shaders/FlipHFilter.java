package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class FlipHFilter
extends BaseFilter {

	public static FlipHFilter instance;
	
	public FlipHFilter(PApplet p) {
		super(p, "shaders/filters/flip-h.glsl");
	}
	
	public static FlipHFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new FlipHFilter(p);
		return instance;
	}

}
