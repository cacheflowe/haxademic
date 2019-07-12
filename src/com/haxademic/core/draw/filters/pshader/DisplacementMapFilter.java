package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.media.DemoAssets;

import processing.core.PApplet;
import processing.core.PImage;

public class DisplacementMapFilter
extends BaseFragmentShader {

	public static DisplacementMapFilter instance;
	
	public DisplacementMapFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/displacement-map.glsl");
		setMap(DemoAssets.smallTexture());
		setAmp(0.1f);
		setMode(1);
		setDivider(4f);
	}
	
	public static DisplacementMapFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new DisplacementMapFilter(p);
		return instance;
	}
	
	public void setMap(PImage texture) {
		shader.set("map", texture);
	}
	
	public void setAmp(float amp) {
		shader.set("amp", amp);
	}
	
	public void setMode(int mode) {
		shader.set("mode", mode);
	}
	
	public void setDivider(float divider) {
		shader.set("divider", divider);
	}
	
}
