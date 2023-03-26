package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class Pixelate2Filter
extends BaseFragmentShader {

	public static Pixelate2Filter instance;
	
	public Pixelate2Filter() {
		super("haxademic/shaders/filters/pixelate2.glsl");
		setDivider(0.2f);
	}
	
	public static Pixelate2Filter instance() {
		if(instance != null) return instance;
		instance = new Pixelate2Filter();
		return instance;
	}
	
	public void setDivider(float divider) {
		shader.set("divider", divider);
	}
	
}
