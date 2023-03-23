package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;
import processing.core.PGraphics;

public class RotateFilter
extends BaseFragmentShader {

	public static RotateFilter instance;
	
	public RotateFilter() {
		super("haxademic/shaders/filters/rotate.glsl");
		setRotation(0f);
		setZoom(1f);
		setOffset(0f, 0f);
	}
	
	public static RotateFilter instance() {
		if(instance != null) return instance;
		instance = new RotateFilter();
		return instance;
	}
	
	public void applyTo(PGraphics pg) {
		shader.set("textureDupe", pg);
		super.applyTo(pg);
	}
	
	public void applyTo(PApplet p) {
		shader.set("textureDupe", p.g);
		super.applyTo(p);
	}
	
	public void setRotation(float rotation) {
		shader.set("rotation", rotation);
	}
	
	public void setZoom(float zoom) {
		shader.set("zoom", zoom);
	}
	
	public void setOffset(float x, float y) {
		shader.set("offset", x, y);
	}
	
}
