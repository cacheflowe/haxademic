package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class ColorDistortionFilter
extends BaseFilter {

	public static ColorDistortionFilter instance;
	
	public ColorDistortionFilter(PApplet p) {
		super(p, "shaders/filters/color-distortion.glsl");
		setAmplitude(1f);
	}
	
	public static ColorDistortionFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ColorDistortionFilter(p);
		return instance;
	}
	
	public void setAmplitude(float amplitude) {
		shader.set("amplitude", amplitude);
	}
	
}
