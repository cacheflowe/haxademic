package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class WobbleFilter
extends BaseFragmentShader {

	public static WobbleFilter instance;
	
	public WobbleFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/wobble.glsl");
		setSpeed(1f);
		setStrength(0.001f);
		setSize(100f);
	}
	
	public static WobbleFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new WobbleFilter(p);
		return instance;
	}
	
	public void setSpeed(float speed) {
		shader.set("speed", speed);
	}
	
	public void setStrength(float strength) {
		shader.set("strength", strength);
	}
	
	public void setSize(float size) {
		shader.set("size", size);
	}
	
}
