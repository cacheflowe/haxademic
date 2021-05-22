package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class RadialFlareFilter
extends BaseFragmentShader {

	public static RadialFlareFilter instance;
	
	public RadialFlareFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/radial-flare.glsl");
		setRadialLength(0.95f);
		setImageBrightness(9f);
		setFlareBrightness(9f);
		setIters(100f);
	}
	
	public static RadialFlareFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new RadialFlareFilter(p);
		return instance;
	}
	
	public void setRadialLength(float radialLength) {
		shader.set("radialLength", radialLength);
	}
	
	public void setImageBrightness(float imageBrightness) {
		shader.set("imageBrightness", imageBrightness);
	}
	
	public void setFlareBrightness(float flareBrightness) {
		shader.set("flareBrightness", flareBrightness);
	}
	
	public void setIters(float iters) {
		shader.set("iters", iters);
	}
	
}
