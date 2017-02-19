package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class WarperFilter
extends BaseFilter {

	public static WarperFilter instance;
	
	public WarperFilter(PApplet p) {
		super(p, "shaders/filters/warper.glsl");
	}
	
	public static WarperFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new WarperFilter(p);
		return instance;
	}
	
}
