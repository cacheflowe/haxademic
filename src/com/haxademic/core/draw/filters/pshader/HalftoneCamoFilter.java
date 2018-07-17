package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class HalftoneCamoFilter
extends BaseFragmentShader {

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
