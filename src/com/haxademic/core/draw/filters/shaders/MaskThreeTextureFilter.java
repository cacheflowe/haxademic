package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;
import com.haxademic.core.file.DemoAssets;

import processing.core.PApplet;
import processing.core.PImage;

public class MaskThreeTextureFilter
extends BaseFilter {

	public static MaskThreeTextureFilter instance;
	
	public MaskThreeTextureFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/three-texture-opposite-mask.glsl");
		setMask(DemoAssets.smallTexture());
		setTexture1(DemoAssets.justin());
		setTexture2(DemoAssets.textureNebula());
	}
	
	public static MaskThreeTextureFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new MaskThreeTextureFilter(p);
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
