package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class ColorSolidFilter
extends BaseFragmentShader {

	public static ColorSolidFilter instance;
	
	public ColorSolidFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/color-solid.glsl");
		setR(1f);
		setG(1f);
		setB(1f);
		setA(1f);
		setCrossfade(1f);
	}
	
	public static ColorSolidFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ColorSolidFilter(p);
		return instance;
	}
	
	public void setR(float r) {
		shader.set("r", r);
	}
	
	public void setG(float g) {
		shader.set("g", g);
	}

	public void setB(float b) {
		shader.set("b", b);
	}

	public void setA(float a) {
		shader.set("a", a);
	}

	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
