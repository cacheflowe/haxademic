package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class Pixelate2Filter
extends BaseFragmentShader {

	public static Pixelate2Filter instance;
	
	public Pixelate2Filter(PApplet p) {
		super(p, "haxademic/shaders/filters/pixelate2.glsl");
		setDivider(0.2f);
	}
	
	public static Pixelate2Filter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new Pixelate2Filter(p);
		return instance;
	}
	
	public void setDivider(float divider) {
		shader.set("divider", divider);
	}
	
}
