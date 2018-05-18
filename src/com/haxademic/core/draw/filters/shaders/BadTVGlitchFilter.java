package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class BadTVGlitchFilter
extends BaseFilter {

	public static BadTVGlitchFilter instance;
	
	public BadTVGlitchFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/badtv2.glsl");
//		setGrayscale(0);
//		setIntensityN(0.55f);
//		setIntensityS(0.45f);
//		setCountS(4096.0f);
	}
	
	public static BadTVGlitchFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BadTVGlitchFilter(p);
		return instance;
	}
	
//	public void setGrayscale(int grayscale) {
//		shader.set("grayscale", grayscale);
//	}
//	
//	public void setIntensityN(float nIntensity) {
//		shader.set("nIntensity", nIntensity);
//	}
//	
//	public void setIntensityS(float sIntensity) {
//		shader.set("sIntensity", sIntensity);
//	}
//	
//	public void setCountS(float sCount) {
//		shader.set("sCount", sCount);
//	}
	
}