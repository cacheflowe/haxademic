package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class ColorizeTwoColorsFilter
extends BaseFragmentShader {

	public static ColorizeTwoColorsFilter instance;
	
	public ColorizeTwoColorsFilter() {
		super("haxademic/shaders/filters/colorize-two-colors.glsl");
		setColor1(1f, 1f, 1f);
		setColor2(0f, 0f, 0f);
		setCrossfadeMode(0);
	}
	
	public static ColorizeTwoColorsFilter instance() {
		if(instance != null) return instance;
		instance = new ColorizeTwoColorsFilter();
		return instance;
	}
	
	public void setColor1(float r, float g, float b) {
		shader.set("color1", r, g, b);
	}
	
	public void setColor2(float r, float g, float b) {
		shader.set("color2", r, g, b);
	}

	public void setCrossfadeMode(int crossfadeMode) {
		shader.set("crossfadeMode", crossfadeMode);
	}
	
}
