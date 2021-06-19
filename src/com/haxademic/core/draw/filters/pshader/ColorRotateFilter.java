package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class ColorRotateFilter
extends BaseFragmentShader {

	public static ColorRotateFilter instance;
	
	public ColorRotateFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/color-rotate.glsl");
		setCrossfade(1f);
		setRotate(0f);
	}
	
	public static ColorRotateFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ColorRotateFilter(p);
		return instance;
	}
	
	public void setRotate(float rotate) {
		shader.set("rotate", rotate);
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
