package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class FXAAFilter
extends BaseFilter {

	public static FXAAFilter instance;
	
	public FXAAFilter(PApplet p) {
		super(p, "shaders/filters/fxaa.glsl");
	}
	
	public static FXAAFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new FXAAFilter(p);
		return instance;
	}

}
