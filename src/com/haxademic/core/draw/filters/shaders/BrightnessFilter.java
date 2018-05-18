package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class BrightnessFilter
extends BaseFilter {

	public static BrightnessFilter instance;
	
	public BrightnessFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/brightness.glsl");
		setBrightness(1f);
	}
	
	public static BrightnessFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BrightnessFilter(p);
		return instance;
	}
	
	public void setBrightness(float brightness) {
		shader.set("brightness", brightness);
	}
	
}
