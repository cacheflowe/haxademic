package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class PixelateFilter
extends BaseFilter {

	public static PixelateFilter instance;
	
	public PixelateFilter(PApplet p) {
		super(p, "shaders/filters/pixelate.glsl");
		setDivider(20f, 20f);
	}
	
	public static PixelateFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new PixelateFilter(p);
		return instance;
	}
	
	public void setDivider(float dividerX, float dividerY) {
		shader.set("divider", dividerX, dividerY);
	}
	
}
