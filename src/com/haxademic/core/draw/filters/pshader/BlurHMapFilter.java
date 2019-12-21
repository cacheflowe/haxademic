package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PApplet;
import processing.core.PImage;

public class BlurHMapFilter
extends BaseFragmentShader {

	public static BlurHMapFilter instance;
	
	public BlurHMapFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/blur-horizontal-map.glsl");
		setMap(DemoAssets.smallTexture());
		setBlur(0f);
	}
	
	public static BlurHMapFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BlurHMapFilter(p);
		return instance;
	}
	
	public void setMap(PImage texture) {
		shader.set("map", texture);
	}
	
	public void setBlur(float blur) {
		shader.set("h", blur);
	}
	
	public void setBlurByPercent(float blurPercent, float imageWidth) {
		shader.set("h", blurPercent * (1f / imageWidth));
	}

	
}
