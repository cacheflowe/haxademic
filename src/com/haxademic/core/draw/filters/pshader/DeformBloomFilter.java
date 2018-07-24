package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class DeformBloomFilter
extends BaseFragmentShader {

	public static DeformBloomFilter instance;
	
	public DeformBloomFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/deform-p5.glsl");
	}
	
	public static DeformBloomFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new DeformBloomFilter(p);
		return instance;
	}

}
