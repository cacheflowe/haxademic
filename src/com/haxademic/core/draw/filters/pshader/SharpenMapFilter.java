package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PApplet;
import processing.core.PImage;

public class SharpenMapFilter
extends BaseFragmentShader {

	public static SharpenMapFilter instance;
	
	public SharpenMapFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/sharpen-map.glsl");
		setMap(DemoAssets.smallTexture());
		setAmpMax(1f);
		setAmpMin(0f);
	}
	
	public static SharpenMapFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new SharpenMapFilter(p);
		return instance;
	}

	public void setMap(PImage texture) {
		shader.set("ampMap", texture);
	}
	
	public void setAmpMax(float ampMax) {
		shader.set("ampMax", ampMax);
	}
	
	public void setAmpMin(float ampMin) {
		shader.set("ampMin", ampMin);
	}
	
}
