package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class FXAAFilter
extends BaseFilter {

	public static FXAAFilter instance;
	
	public FXAAFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/fxaa.glsl");
	}
	
	public static FXAAFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new FXAAFilter(p);
		return instance;
	}

}
