package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class InvertFilter
extends BaseFilter {

	public static InvertFilter instance;
	
	public InvertFilter(PApplet p) {
		super(p, "shaders/filters/invert.glsl");
	}
	
	public static InvertFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new InvertFilter(p);
		return instance;
	}

}
