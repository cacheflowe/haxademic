package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class BulgeLinearFilter
extends BaseFragmentShader {

	public static BulgeLinearFilter instance;
	
	public BulgeLinearFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/bulge-linear.glsl");
		setControlX(0.5f);
		setMixAmp(0.1f);
		setGainCurve(1.2f);
	}
	
	public static BulgeLinearFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BulgeLinearFilter(p);
		return instance;
	}
	
	public void setControlX(float controlX) {
		shader.set("controlX", controlX);
	}
	
	public void setMixAmp(float mixAmp) {
		shader.set("mixAmp", mixAmp);
	}
	
	public void setGainCurve(float gainCurve) {
		shader.set("gainCurve", gainCurve);
	}
	
	public void setDebug(boolean debug) {
		shader.set("debug", debug);
	}
	
}