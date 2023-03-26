package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PImage;

public class BlendTextureScreen
extends BaseFragmentShader {

	public static BlendTextureScreen instance;
	
	public BlendTextureScreen() {
		super("haxademic/shaders/filters/blend-texture-screen.glsl");
	}
	
	public static BlendTextureScreen instance() {
		if(instance != null) return instance;
		instance = new BlendTextureScreen();
		return instance;
	}
	
	public void setSourceTexture(PImage source) {
		shader.set("targetTexture", source);
	}
	
}
