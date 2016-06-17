package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class EdgeColorDarkenFilter
extends BaseFilter {

	public static EdgeColorDarkenFilter instance;
	
	public EdgeColorDarkenFilter(PApplet p) {
		super(p, "shaders/filters/edge-color-darken.glsl");
		setSpreadX(0.05f);
		setSpreadY(0.05f);
	}
	
	public static EdgeColorDarkenFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new EdgeColorDarkenFilter(p);
		return instance;
	}

	public void setSpreadX(float spreadX) {
		shader.set("spreadX", spreadX);
	}

	public void setSpreadY(float spreadY) {
		shader.set("spreadY", spreadY);
	}
	
}
