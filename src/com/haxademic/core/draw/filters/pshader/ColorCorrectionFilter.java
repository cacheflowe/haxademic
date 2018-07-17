package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class ColorCorrectionFilter
extends BaseFragmentShader {

	public static ColorCorrectionFilter instance;
	
	public ColorCorrectionFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/color-correction.glsl");
		setBrightness(0f);
		setContrast(1f);
		setGamma(1f);
	}
	
	public static ColorCorrectionFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ColorCorrectionFilter(p);
		return instance;
	}
	
	public void setBrightness(float brightness) {
		shader.set("brightness", brightness);
	}
	
	public void setContrast(float contrast) {
		shader.set("contrast", contrast);
	}
	
	public void setGamma(float gamma) {
		shader.set("gamma", gamma);
	}
	
}
