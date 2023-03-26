package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class PixelateFilter
extends BaseFragmentShader {

	public static PixelateFilter instance;
	
	public PixelateFilter() {
		super("haxademic/shaders/filters/pixelate.glsl");
		setDivider(20f, P.p.width, P.p.height);
	}
	
	public static PixelateFilter instance() {
		if(instance != null) return instance;
		instance = new PixelateFilter();
		return instance;
	}
	
	public void setDivider(float divider, float imageW, float imageH) {
		shader.set("divider", (float)P.round(imageW/divider), (float)P.round(imageH/divider));
	}
	
}
