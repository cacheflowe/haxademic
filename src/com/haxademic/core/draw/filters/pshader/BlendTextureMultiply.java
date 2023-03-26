package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PImage;

public class BlendTextureMultiply
extends BaseFragmentShader {

	public static BlendTextureMultiply instance;
	
	public BlendTextureMultiply() {
		super("haxademic/shaders/filters/blend-texture-multiply.glsl");
	}
	
	public static BlendTextureMultiply instance() {
		if(instance != null) return instance;
		instance = new BlendTextureMultiply();
		return instance;
	}
	
	public void setSourceTexture(PImage source) {
		shader.set("targetTexture", source);
	}
	
}
