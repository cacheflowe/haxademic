package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class LeaveBlackFilter
extends BaseFilter {

	public static LeaveBlackFilter instance;
	
	public LeaveBlackFilter(PApplet p) {
		super(p, "shaders/filters/leave-black.glsl");
		setMix(1f);
	}
	
	public static LeaveBlackFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new LeaveBlackFilter(p);
		return instance;
	}
	
	public void setMix(float mix) {
		shader.set("mix", mix);
	}
	
}
