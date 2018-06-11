package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;
import processing.core.PImage;

public class BlendTowardsTexture
extends BaseFilter {

	public static BlendTowardsTexture instance;
	
	public BlendTowardsTexture(PApplet p) {
		super(p, "haxademic/shaders/filters/texture-blend-towards-texture.glsl");
		setBlendLerp(0.25f);
	}
	
	public static BlendTowardsTexture instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BlendTowardsTexture(p);
		return instance;
	}
	
	public void setBlendLerp(float blendLerp) {
		shader.set("blendLerp", blendLerp);
	}
	
	public void setSourceTexture(PImage source) {
		shader.set("targetTexture", source);
	}
	
}
