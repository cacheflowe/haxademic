package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class ChromaColorFilter
extends BaseFilter {

	public static ChromaColorFilter instance;
	
	public ChromaColorFilter(PApplet p) {
		super(p, "shaders/filters/chroma-color.glsl");
		setThresholdSensitivity(0.1f);
		setSmoothing(0.7f);
		setColorToReplace(0, 0, 0);
	}
	
	public static ChromaColorFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ChromaColorFilter(p);
		return instance;
	}
	
	public void setThresholdSensitivity(float thresholdSensitivity) {
		shader.set("thresholdSensitivity", thresholdSensitivity);
	}
	
	public void setSmoothing(float smoothing) {
		shader.set("smoothing", smoothing);
	}
	
	public void setColorToReplace(float colorToReplaceR, float colorToReplaceG, float colorToReplaceB) {
		shader.set("colorToReplace", colorToReplaceR, colorToReplaceG, colorToReplaceB);
	}
	
}
