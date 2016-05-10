package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class SharpenFilter
extends BaseFilter {

	public static SharpenFilter instance;
	
	public SharpenFilter(PApplet p) {
		super(p, "shaders/filters/sharpen.glsl");
	}
	
	public static SharpenFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new SharpenFilter(p);
		return instance;
	}

}
