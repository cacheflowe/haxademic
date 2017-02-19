package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class EmbossFilter
extends BaseFilter {

	public static EmbossFilter instance;
	
	public EmbossFilter(PApplet p) {
		super(p, "shaders/filters/emboss.glsl");
	}
	
	public static EmbossFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new EmbossFilter(p);
		return instance;
	}

}
