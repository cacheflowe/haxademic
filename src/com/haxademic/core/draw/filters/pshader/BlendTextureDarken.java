package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PImage;

public class BlendTextureDarken
extends BaseFragmentShader {

	public static BlendTextureDarken instance;
	
	public BlendTextureDarken() {
		super("haxademic/shaders/filters/blend-texture-darken.glsl");
	}
	
	public static BlendTextureDarken instance() {
		if(instance != null) return instance;
		instance = new BlendTextureDarken();
		return instance;
	}
	
	public void setSourceTexture(PImage source) {
		shader.set("targetTexture", source);
	}
	
}
