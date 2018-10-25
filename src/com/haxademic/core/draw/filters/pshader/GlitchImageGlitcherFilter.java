package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class GlitchImageGlitcherFilter
extends BaseFragmentShader {

	public static GlitchImageGlitcherFilter instance;
	
	public GlitchImageGlitcherFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/glitch-imageglitcher.glsl");
		setColorSeparation(true);
		setAmp(1f);
		setCrossfade(1f);
		setGlitchSpeed(0.16f);
		setBarSize(0.25f);
		setNumSlices(10.0f);
	}
	
	public static GlitchImageGlitcherFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new GlitchImageGlitcherFilter(p);
		return instance;
	}
	
	public void setColorSeparation(boolean colorSeparation) {
		shader.set("colorSeparation", colorSeparation);
	}
	
	public void setAmp(float amp) {
		shader.set("amp", amp);
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
	public void setGlitchSpeed(float glitchSpeed) {
		shader.set("glitchSpeed", glitchSpeed);
	}
	
	public void setBarSize(float barSize) {
		shader.set("barSize", barSize);
	}
	
	public void setNumSlices(float numSlices) {
		shader.set("numSlices", numSlices);
	}
	
}
