package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class GlitchShakeFilter
extends BaseFragmentShader {

	public static GlitchShakeFilter instance;
	
	public GlitchShakeFilter() {
		super("haxademic/shaders/filters/glitch-shake.glsl");
		setAmp(1f);
		setGlitchSpeed(0.16f);
		setCrossfade(1f);
		setSubdivide1(64f);
		setSubdivide2(64f);
	}
	
	public static GlitchShakeFilter instance() {
		if(instance != null) return instance;
		instance = new GlitchShakeFilter();
		return instance;
	}
	
	public void setAmp(float amp) {
		shader.set("amp", amp);
	}
	
	public void setGlitchSpeed(float glitchSpeed) {
		shader.set("glitchSpeed", glitchSpeed);
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
	public void setSubdivide1(float subdivide1) {
		shader.set("subdivide1", subdivide1);
	}
	
	public void setSubdivide2(float subdivide2) {
		shader.set("subdivide2", subdivide2);
	}
}
