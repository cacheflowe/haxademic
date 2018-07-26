package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class LeaveWhiteFilter
extends BaseFragmentShader {

	public static LeaveWhiteFilter instance;
	
	public LeaveWhiteFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/leave-white.glsl");
		setMix(1f);
	}
	
	public static LeaveWhiteFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new LeaveWhiteFilter(p);
		return instance;
	}
	
	public void setMix(float mix) {
		shader.set("mix", mix);
	}
	
}
