package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class HalftoneCamoFilter
extends BaseFilter {

	public static HalftoneCamoFilter instance;
	
	public HalftoneCamoFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/halftone-camo-filter.glsl");
		setScale(1f);
	}
	
	public static HalftoneCamoFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new HalftoneCamoFilter(p);
		return instance;
	}

	public void setScale(float scale) {
		shader.set("scale", scale);
	}

}
