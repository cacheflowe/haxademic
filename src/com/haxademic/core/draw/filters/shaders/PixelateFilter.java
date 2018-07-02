package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class PixelateFilter
extends BaseFilter {

	public static PixelateFilter instance;
	
	public PixelateFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/pixelate.glsl");
		setDivider(20f, p.width, p.height);
	}
	
	public static PixelateFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new PixelateFilter(p);
		P.println("Make sure PixelateFilter divisor is an integer. Processing's 8-bit buffers cause seamsotherwise.");
		return instance;
	}
	
	public void setDivider(float divider, float imageW, float imageH) {
		shader.set("divider", (float)P.round(imageW/divider), (float)P.round(imageH/divider));
	}
	
}
