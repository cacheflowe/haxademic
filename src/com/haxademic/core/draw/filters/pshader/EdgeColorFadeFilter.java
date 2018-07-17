package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class EdgeColorFadeFilter
extends BaseFragmentShader {

	public static EdgeColorFadeFilter instance;
	
	public EdgeColorFadeFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/edge-color-fade.glsl");
		setSpreadX(0.05f);
		setSpreadY(0.05f);
		setEdgeColor(255f, 255f, 255f);
	}
	
	public static EdgeColorFadeFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new EdgeColorFadeFilter(p);
		return instance;
	}

	public void setSpreadX(float spreadX) {
		shader.set("spreadX", spreadX);
	}

	public void setSpreadY(float spreadY) {
		shader.set("spreadY", spreadY);
	}

	public void setEdgeColor(float colorR, float colorG, float colorB) {
		shader.set("edgeColor", colorR/255f, colorG/255f, colorB/255f);
	}

}
