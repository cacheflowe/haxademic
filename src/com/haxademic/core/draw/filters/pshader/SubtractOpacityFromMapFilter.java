package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PApplet;
import processing.core.PImage;

public class SubtractOpacityFromMapFilter
extends BaseFragmentShader {

	public static SubtractOpacityFromMapFilter instance;
	
	public SubtractOpacityFromMapFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/opaque-pixels-subtract-opacity-from-map.glsl");
		setMap(DemoAssets.smallTexture());
	}
	
	public static SubtractOpacityFromMapFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new SubtractOpacityFromMapFilter(p);
		return instance;
	}
	
	public void setMap(PImage opacityMap) {
		shader.set("opacityMap", opacityMap);
	}
	
}
