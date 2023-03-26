package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class ColorRotateFilter
extends BaseFragmentShader {

	public static ColorRotateFilter instance;
	
	public ColorRotateFilter() {
		super("haxademic/shaders/filters/color-rotate.glsl");
		setCrossfade(1f);
		setRotate(0f);
	}
	
	public static ColorRotateFilter instance() {
		if(instance != null) return instance;
		instance = new ColorRotateFilter();
		return instance;
	}
	
	public void setRotate(float rotate) {
		shader.set("rotate", rotate);
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
