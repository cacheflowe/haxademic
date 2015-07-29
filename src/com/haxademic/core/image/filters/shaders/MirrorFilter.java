package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class MirrorFilter
extends BaseFilter {

	public static MirrorFilter instance;
	
	public MirrorFilter(PApplet p) {
		super(p, "shaders/filters/mirror.glsl");
	}
	
	public static MirrorFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new MirrorFilter(p);
		return instance;
	}

}
