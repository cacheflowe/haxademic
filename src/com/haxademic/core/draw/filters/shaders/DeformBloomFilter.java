package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class DeformBloomFilter
extends BaseFilter {

	public static DeformBloomFilter instance;
	
	public DeformBloomFilter(PApplet p) {
		super(p, "shaders/filters/deform-p5.glsl");
	}
	
	public static DeformBloomFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new DeformBloomFilter(p);
		return instance;
	}

}
