package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PImage;

public class FeedbackMapFilter
extends BaseFragmentShader {

	public static FeedbackMapFilter instance;
	
	public FeedbackMapFilter() {
		super("haxademic/shaders/filters/feedback-map.glsl");
		setMap(DemoAssets.smallTexture());
		setAmp(0.1f);
		setBrightnessStep(-1f/255f);
		setAlphaStep(-1f/255f);
		setRadiansStart(0f);
		setRadiansRange(P.TWO_PI * 3f);
	}
	
	public static FeedbackMapFilter instance() {
		if(instance != null) return instance;
		instance = new FeedbackMapFilter();
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
	
	public void setRadiansStart(float radiansStart) {
		shader.set("radiansStart", radiansStart);
	}
	
	public void setRadiansRange(float radiansRange) {
		shader.set("radiansRange", radiansRange);
	}
	
}
