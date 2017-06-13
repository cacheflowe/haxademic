package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class ColorizeFilter
extends BaseFilter {

	public static ColorizeFilter instance;
	
	public ColorizeFilter(PApplet p) {
		super(p, "shaders/filters/colorize.glsl");
		setTargetR(1f);
		setTargetG(1f);
		setTargetB(1f);
	}
	
	public static ColorizeFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ColorizeFilter(p);
		return instance;
	}
	
	public void setTargetR(float targetR) {
		shader.set("targetR", targetR);
	}
	
	public void setTargetG(float targetG) {
		shader.set("targetG", targetG);
	}

	public void setTargetB(float targetB) {
		shader.set("targetB", targetB);
	}

}
