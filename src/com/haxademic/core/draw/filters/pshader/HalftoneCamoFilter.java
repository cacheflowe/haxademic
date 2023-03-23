package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class HalftoneCamoFilter
extends BaseFragmentShader {

	public static HalftoneCamoFilter instance;
	
	public HalftoneCamoFilter() {
		super("haxademic/shaders/filters/halftone-camo-filter.glsl");
		setScale(1f);
	}
	
	public static HalftoneCamoFilter instance() {
		if(instance != null) return instance;
		instance = new HalftoneCamoFilter();
		return instance;
	}

	public void setScale(float scale) {
		shader.set("scale", scale);
	}

}
