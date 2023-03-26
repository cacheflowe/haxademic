package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class DeformBloomFilter
extends BaseFragmentShader {

	public static DeformBloomFilter instance;
	
	public DeformBloomFilter() {
		super("haxademic/shaders/filters/deform-p5.glsl");
	}
	
	public static DeformBloomFilter instance() {
		if(instance != null) return instance;
		instance = new DeformBloomFilter();
		return instance;
	}

}
