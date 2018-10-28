package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class LumaColorReplaceFilter
extends BaseFragmentShader {

	public static LumaColorReplaceFilter instance;
	
	public LumaColorReplaceFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/luma-color-replace.glsl");
		setTargetColor(1f, 0.274f, 0.023f, 1f);
		setDiffRange(0.1f);
		setLumaTarget(0.5f);
	}
	
	public static LumaColorReplaceFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new LumaColorReplaceFilter(p);
		return instance;
	}
	
	public void setTargetColor(float r, float g, float b, float a) {
		shader.set("targetColor", r, g, b, a);
	}
	
	public void setDiffRange(float diffRange) {
		shader.set("diffRange", diffRange);
	}
	
	public void setLumaTarget(float lumaTarget) {
		shader.set("lumaTarget", lumaTarget);
	}
}
