package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class ChromaColorFilter
extends BaseFragmentShader {

	public static ChromaColorFilter instance;
	
	public ChromaColorFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/chroma-color.glsl");
		setThresholdSensitivity(0.1f);
		setSmoothing(0.7f);
		setColorToReplace(0, 0, 0);
	}
	
	public static ChromaColorFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ChromaColorFilter(p);
		return instance;
	}
	
	public void setThresholdSensitivity(float thresholdSensitivity) {
		shader.set("thresholdSensitivity", thresholdSensitivity);
	}
	
	public void setSmoothing(float smoothing) {
		shader.set("smoothing", smoothing);
	}
	
	public void setColorToReplace(float colorToReplaceR, float colorToReplaceG, float colorToReplaceB) {
		shader.set("colorToReplace", colorToReplaceR, colorToReplaceG, colorToReplaceB);
	}
	
	public ChromaColorFilter presetGreenScreen() {
		setThresholdSensitivity(0.73f);
		setSmoothing(0.08f);
		setColorToReplace(0.71f, 0.99f, 0.02f);
		return instance(P.p);
	}
	
	public ChromaColorFilter presetBlackKnockout() {
		setThresholdSensitivity(0.2f);
		setSmoothing(0.1f); // 0.25>
		setColorToReplace(0.0f, 0.0f, 0.0f);
		return instance(P.p);
	}

	public ChromaColorFilter presetWhiteKnockout() {
		setThresholdSensitivity(0.2f);
		setSmoothing(0.1f);
		setColorToReplace(1.0f, 1.0f, 1.0f);
		return instance(P.p);
	}
}
