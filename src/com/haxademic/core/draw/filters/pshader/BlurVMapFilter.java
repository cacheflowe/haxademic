package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PApplet;
import processing.core.PImage;

public class BlurVMapFilter
extends BaseFragmentShader {

	public static BlurVMapFilter instance;
	
	public BlurVMapFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/blur-vertical-map.glsl");
		setMap(DemoAssets.smallTexture());
		setBlur(0.001f);
	}
	
	public static BlurVMapFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BlurVMapFilter(p);
		return instance;
	}
	
	public void setMap(PImage texture) {
		shader.set("map", texture);
	}
	
	public void setBlur(float blur) {
		shader.set("v", blur);
	}
	
	public void setBlurByPercent(float blurPercent, float imageWidth) {
		shader.set("v", blurPercent * (1f / imageWidth));
	}
	
}
