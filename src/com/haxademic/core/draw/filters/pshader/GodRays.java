package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class GodRays
extends BaseFragmentShader {

	public static GodRays instance;
	
	public GodRays(PApplet p) {
		super(p, "haxademic/shaders/filters/godrays.glsl");
		setDecay(0.97f);
		setDensity(0.5f);
		setWeight(0.1f);
		setRotation(0f);
		setAmp(1f);
	}
	
	public static GodRays instance(PApplet p) {
		if(instance != null) return instance;
		instance = new GodRays(p);
		return instance;
	}
	
	public void setDecay(float decay) {
		shader.set("decay", decay);
	}
	
	public void setDensity(float density) {
		shader.set("density", density);
	}
	
	public void setWeight(float weight) {
		shader.set("weight", weight);
	}
	
	public void setRotation(float rotation) {
		shader.set("rotation", rotation);
	}
	
	public void setAmp(float amp) {
		shader.set("amp", amp);
	}
	
}
