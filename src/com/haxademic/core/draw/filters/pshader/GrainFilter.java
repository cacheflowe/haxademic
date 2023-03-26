package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class GrainFilter
extends BaseFragmentShader {

	public static GrainFilter instance;
	
	public GrainFilter() {
		super("haxademic/shaders/filters/grain.glsl");
		setCrossfade(0.1f);
		setTime(0f);
	}
	
	public static GrainFilter instance() {
		if(instance != null) return instance;
		instance = new GrainFilter();
		return instance;
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
		
}