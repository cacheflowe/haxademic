package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;
import processing.core.PImage;

public class BlendTextureDarken
extends BaseFragmentShader {

	public static BlendTextureDarken instance;
	
	public BlendTextureDarken(PApplet p) {
		super(p, "haxademic/shaders/filters/blend-texture-darken.glsl");
	}
	
	public static BlendTextureDarken instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BlendTextureDarken(p);
		return instance;
	}
	
	public void setSourceTexture(PImage source) {
		shader.set("targetTexture", source);
	}
	
}
