package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class MirrorFilter
extends BaseFragmentShader {

	public static MirrorFilter instance;
	
	public MirrorFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/mirror.glsl");
		setHorizontal(true);
	}
	
	public static MirrorFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new MirrorFilter(p);
		return instance;
	}

	public void setHorizontal(boolean horizontal) {
		shader.set("horizontal", horizontal);
	}
	

}
