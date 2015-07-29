package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class SphereDistortionFilter
extends BaseFilter {

	public static SphereDistortionFilter instance;
	
	public SphereDistortionFilter(PApplet p) {
		super(p, "shaders/filters/sphere-distortion.glsl");
	}
	
	public static SphereDistortionFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new SphereDistortionFilter(p);
		return instance;
	}

}
