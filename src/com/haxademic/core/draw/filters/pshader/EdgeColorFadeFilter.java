package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class EdgeColorFadeFilter
extends BaseFragmentShader {

	public static EdgeColorFadeFilter instance;
	
	public EdgeColorFadeFilter() {
		super("haxademic/shaders/filters/edge-color-fade.glsl");
		setSpreadX(0.05f);
		setSpreadY(0.05f);
		setEdgeColor(1f, 1f, 1f);
		setCrossfade(1f);
	}
	
	public static EdgeColorFadeFilter instance() {
		if(instance != null) return instance;
		instance = new EdgeColorFadeFilter();
		return instance;
	}

	public void setSpreadX(float spreadX) {
		shader.set("spreadX", spreadX);
	}

	public void setSpreadY(float spreadY) {
		shader.set("spreadY", spreadY);
	}
	
	public void setEdgeColor(float colorR, float colorG, float colorB) {
		shader.set("edgeColor", colorR, colorG, colorB);
	}

	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
