package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class ErosionFilter
extends BaseFilter {

	public static ErosionFilter instance;
	
	public ErosionFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/erosion.glsl");
	}
	
	public static ErosionFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ErosionFilter(p);
		return instance;
	}

}
