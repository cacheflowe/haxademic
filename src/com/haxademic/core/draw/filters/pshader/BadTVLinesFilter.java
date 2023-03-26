package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class BadTVLinesFilter
extends BaseFragmentShader {

	public static BadTVLinesFilter instance;
	
	public BadTVLinesFilter() {
		super("haxademic/shaders/filters/badtv.glsl");
		setGrayscale(0);
		setIntensityN(0.55f);
		setIntensityS(0.45f);
		setCountS(4096.0f);
	}
	
	public static BadTVLinesFilter instance() {
		if(instance != null) return instance;
		instance = new BadTVLinesFilter();
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