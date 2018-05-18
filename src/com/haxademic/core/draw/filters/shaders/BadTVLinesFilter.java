package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class BadTVLinesFilter
extends BaseFilter {

	public static BadTVLinesFilter instance;
	
	public BadTVLinesFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/badtv.glsl");
		setGrayscale(0);
		setIntensityN(0.55f);
		setIntensityS(0.45f);
		setCountS(4096.0f);
	}
	
	public static BadTVLinesFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BadTVLinesFilter(p);
		return instance;
	}
	
	public void setGrayscale(int grayscale) {
		shader.set("grayscale", grayscale);
	}
	
	public void setIntensityN(float nIntensity) {
		shader.set("nIntensity", nIntensity);
	}
	
	public void setIntensityS(float sIntensity) {
		shader.set("sIntensity", sIntensity);
	}
	
	public void setCountS(float sCount) {
		shader.set("sCount", sCount);
	}
	
}