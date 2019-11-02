package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PApplet;
import processing.core.PImage;

public class FeedbackMapFilter
extends BaseFragmentShader {

	public static FeedbackMapFilter instance;
	
	public FeedbackMapFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/feedback-map.glsl");
		setMap(DemoAssets.smallTexture());
		setAmp(0.1f);
	}
	
	public static FeedbackMapFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new FeedbackMapFilter(p);
		return instance;
	}
	
	public void setMap(PImage texture) {
		shader.set("map", texture);
	}
	
	public void setAmp(float amp) {
		shader.set("amp", amp);
	}
	
	public void setBrightnessStep(float brightnessStep) {
		shader.set("brightnessStep", brightnessStep);
	}
	
	public void setAlphaStep(float alphaStep) {
		shader.set("alphaStep", alphaStep);
	}
	
}
