package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class EdgeColorDarkenFilter
extends BaseFragmentShader {

	public static EdgeColorDarkenFilter instance;
	
	public EdgeColorDarkenFilter() {
		super("haxademic/shaders/filters/edge-color-darken.glsl");
		setSpreadX(0.05f);
		setSpreadY(0.05f);
	}
	
	public static EdgeColorDarkenFilter instance() {
		if(instance != null) return instance;
		instance = new EdgeColorDarkenFilter();
		return instance;
	}
	
	public void setSpreadX(float spreadX) {
		shader.set("spreadX", spreadX);
	}

	public void setSpreadY(float spreadY) {
		shader.set("spreadY", spreadY);
	}
	
}
