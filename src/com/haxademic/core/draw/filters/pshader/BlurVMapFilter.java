package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class BlurVMapFilter
extends BaseFragmentShader {

	public static BlurVMapFilter instance;
	
	public BlurVMapFilter() {
		super("haxademic/shaders/filters/blur-vertical-map.glsl");
		setMap(DemoAssets.smallTexture());
		setAmpMax(1f);
		setAmpMin(0f);
	}
	
	public static BlurVMapFilter instance() {
		if(instance != null) return instance;
		instance = new BlurVMapFilter();
		return instance;
	}

	public void setMap(PImage texture) {
		shader.set("ampMap", texture);
	}
	
	public void setAmpMax(float ampMax) {
		shader.set("ampMax", ampMax);
	}
	
	public void setAmpMin(float ampMin) {
		shader.set("ampMin", ampMin);
	}
	
}
