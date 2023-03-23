package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class MaskThreeTextureFilter
extends BaseFragmentShader {

	public static MaskThreeTextureFilter instance;
	
	public MaskThreeTextureFilter() {
		super("haxademic/shaders/filters/three-texture-opposite-mask.glsl");
		setMask(DemoAssets.smallTexture());
		setTexture1(DemoAssets.justin());
		setTexture2(DemoAssets.textureNebula());
	}
	
	public static MaskThreeTextureFilter instance() {
		if(instance != null) return instance;
		instance = new MaskThreeTextureFilter();
		return instance;
	}
	
	public void setMask(PImage texture) {
		shader.set("mask", texture);
	}
	
	public void setTexture1(PImage texture) {
		shader.set("tex1", texture);
	}
	
	public void setTexture2(PImage texture) {
		shader.set("tex2", texture);
	}
	
}
