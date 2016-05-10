package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class BlurBasicFilter
extends BaseFilter {

	public static BlurBasicFilter instance;
	
	public BlurBasicFilter(PApplet p) {
		super(p, "shaders/filters/blur-basic.glsl");
	}
	
	public static BlurBasicFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BlurBasicFilter(p);
		return instance;
	}
}
