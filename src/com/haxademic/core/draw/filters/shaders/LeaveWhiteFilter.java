package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class LeaveWhiteFilter
extends BaseFilter {

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
