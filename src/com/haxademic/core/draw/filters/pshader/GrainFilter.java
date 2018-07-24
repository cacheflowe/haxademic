package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class GrainFilter
extends BaseFragmentShader {

	public static GrainFilter instance;
	
	public GrainFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/grain.glsl");
		setCrossfade(0.1f);
		setTime(0f);
	}
	
	public static GrainFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new GrainFilter(p);
		return instance;
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
		
}