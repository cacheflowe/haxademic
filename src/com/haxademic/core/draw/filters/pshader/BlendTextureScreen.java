package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;
import processing.core.PImage;

public class BlendTextureScreen
extends BaseFragmentShader {

	public static BlendTextureScreen instance;
	
	public BlendTextureScreen(PApplet p) {
		super(p, "haxademic/shaders/filters/blend-texture-screen.glsl");
	}
	
	public static BlendTextureScreen instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BlendTextureScreen(p);
		return instance;
	}
	
	public void setSourceTexture(PImage source) {
		shader.set("targetTexture", source);
	}
	
}
