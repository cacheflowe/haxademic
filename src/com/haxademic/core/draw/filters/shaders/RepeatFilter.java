package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;
import processing.core.PGraphics;

public class RepeatFilter
extends BaseFilter {

	public static RepeatFilter instance;
	
	public RepeatFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/repeat.glsl");
		setZoom(1f);
		setOffset(0f, 0f);
	}
	
	public static RepeatFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new RepeatFilter(p);
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
