package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class GlitchShaderAFilter
extends BaseFragmentShader {

	public static GlitchShaderAFilter instance;
	
	public GlitchShaderAFilter() {
		super("haxademic/shaders/filters/glitch-shader-a.glsl");
		setAmp(1f);
		setCrossfade(1f);
	}
	
	public static GlitchShaderAFilter instance() {
		if(instance != null) return instance;
		instance = new GlitchShaderAFilter();
		return instance;
	}
	
	public void setAmp(float amp) {
		shader.set("amp", amp);
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
