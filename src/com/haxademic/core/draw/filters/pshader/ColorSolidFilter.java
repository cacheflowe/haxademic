package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class ColorSolidFilter
extends BaseFragmentShader {

	public static ColorSolidFilter instance;
	
	public ColorSolidFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/color-solid.glsl");
		setColor(1f, 1f, 1f, 1f);
		setCrossfade(1f);
	}
	
	public static ColorSolidFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ColorSolidFilter(p);
		return instance;
	}
	
	public void setColor(float r, float g, float b, float a) {
		shader.set("targetColor", r, g, b, a);
	}

	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
