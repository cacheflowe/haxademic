package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PImage;

public class BlendTowardsTexture
extends BaseFragmentShader {

	public static BlendTowardsTexture instance;
	
	public BlendTowardsTexture() {
		super("haxademic/shaders/filters/texture-blend-towards-texture.glsl");
		setBlendLerp(0.25f);
		setFlipY(false);
	}
	
	public static BlendTowardsTexture instance() {
		if(instance != null) return instance;
		instance = new BlendTowardsTexture();
		return instance;
	}
	
	public void setBlendLerp(float blendLerp) {
		shader.set("blendLerp", blendLerp);
	}
	
	public void setSourceTexture(PImage source) {
		shader.set("targetTexture", source);
	}
	
	public void setFlipY(boolean flipY) {
		// needed if source is a PImage instead of PGraphics
		shader.set("flipY", flipY);
	}
	
}
