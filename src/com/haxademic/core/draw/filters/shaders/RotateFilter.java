package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class RotateFilter
extends BaseFilter {

	public static RotateFilter instance;
	
	public RotateFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/rotate.glsl");
		setRotation(0);
		setAspect(1f, 1f);
		setZoom(1f);
		setOffset(0f, 0f);
	}
	
	public static RotateFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new RotateFilter(p);
		return instance;
	}
	
	public void setRotation(float rotation) {
		shader.set("rotation", rotation);
	}
	
	public void setAspect(float width, float height) {
		shader.set("aspect", width / height);
	}
	
	public void setZoom(float zoom) {
		shader.set("zoom", zoom);
	}
	
	public void setOffset(float x, float y) {
		shader.set("offset", x, y);
	}
	
}
