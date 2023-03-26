package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class EmbossFilter
extends BaseFragmentShader {

	public static EmbossFilter instance;
	
	public EmbossFilter() {
		super("haxademic/shaders/filters/emboss.glsl");
	}
	
	public static EmbossFilter instance() {
		if(instance != null) return instance;
		instance = new EmbossFilter();
		return instance;
	}

}
