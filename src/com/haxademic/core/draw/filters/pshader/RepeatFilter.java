package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;
import processing.core.PGraphics;

public class RepeatFilter
extends BaseFragmentShader {

	public static RepeatFilter instance;
	
	public RepeatFilter() {
		super("haxademic/shaders/filters/repeat.glsl");
		setZoom(1f);
		setOffset(0f, 0f);
	}
	
	public static RepeatFilter instance() {
		if(instance != null) return instance;
		instance = new RepeatFilter();
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
	
	public void setZoom(float zoom) {
		shader.set("zoom", zoom);
	}
	
	public void setOffset(float x, float y) {
		shader.set("offset", x, y);
	}
	
	
}
