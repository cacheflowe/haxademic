package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class GradientCoverWipe
extends BaseFragmentShader {

	public static GradientCoverWipe instance;
	
	public GradientCoverWipe() {
		super("haxademic/shaders/filters/gradient-cover-wipe.glsl");
		setColorBot(1f, 1f, 1f, 1f);
		setColorTop(0f, 0f, 0f, 1f);
		setProgress(0);
	}
	
	public static GradientCoverWipe instance() {
		if(instance != null) return instance;
		instance = new GradientCoverWipe();
		return instance;
	}
	
	public void setColorBot(float r, float g, float b, float a) {
		shader.set("colorBot", r, g, b, a);
	}
	
	public void setColorTop(float r, float g, float b, float a) {
		shader.set("colorTop", r, g, b, a);
	}

	public void setGradientEdge(float gradientEdge) {
		shader.set("gradientEdge", gradientEdge);
	}
	
	public void setProgress(float progress) {
		shader.set("progress", progress);
	}
	
}
