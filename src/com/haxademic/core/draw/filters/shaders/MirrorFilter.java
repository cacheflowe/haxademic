package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class MirrorFilter
extends BaseFilter {

	public static MirrorFilter instance;
	
	public MirrorFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/mirror.glsl");
	}
	
	public static MirrorFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new MirrorFilter(p);
		return instance;
	}

}
