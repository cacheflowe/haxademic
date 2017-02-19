package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.app.P;

import processing.core.PApplet;

public class HueFilter
extends BaseFilter {

	public static HueFilter instance;
	
	public HueFilter(PApplet p) {
		super(p, "shaders/filters/hue.glsl");
		setHue(90f);
	}
	
	public static HueFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new HueFilter(p);
		return instance;
	}
	
	public void setHue(float hueAdjust) {
	    hueAdjust = hueAdjust % 360.0f * P.PI/180f;
		shader.set("hueAdjust", hueAdjust);
	}
	
}
