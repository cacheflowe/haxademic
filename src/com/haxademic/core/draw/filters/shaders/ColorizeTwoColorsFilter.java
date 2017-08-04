package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class ColorizeTwoColorsFilter
extends BaseFilter {

	public static ColorizeTwoColorsFilter instance;
	
	public ColorizeTwoColorsFilter(PApplet p) {
		super(p, "shaders/filters/colorize-two-colors.glsl");
		setColor1(1f, 1f, 1f);
		setColor2(0f, 0f, 0f);
		setCrossfade(true);
	}
	
	public static ColorizeTwoColorsFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ColorizeTwoColorsFilter(p);
		return instance;
	}
	
	public void setColor1(float r, float g, float b) {
		shader.set("color1", r, g, b);
	}
	
	public void setColor2(float r, float g, float b) {
		shader.set("color2", r, g, b);
	}

	public void setCrossfade(boolean crossfade) {
		float cross = (crossfade == true) ? 1f : 0f;
		shader.set("crossfade", cross);
	}
	
}
