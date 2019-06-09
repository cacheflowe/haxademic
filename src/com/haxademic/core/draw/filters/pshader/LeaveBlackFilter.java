package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class LeaveBlackFilter
extends BaseFragmentShader {

	public static LeaveBlackFilter instance;
	
	public LeaveBlackFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/leave-black.glsl");
		setCrossfade(1f);
	}
	
	public static LeaveBlackFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new LeaveBlackFilter(p);
		return instance;
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
