package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class WarperFilter
extends BaseFragmentShader {

	public static WarperFilter instance;
	
	public WarperFilter() {
		super("haxademic/shaders/filters/warper.glsl");
	}
	
	public static WarperFilter instance() {
		if(instance != null) return instance;
		instance = new WarperFilter();
		return instance;
	}
	
}
