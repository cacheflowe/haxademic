package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class ThresholdFilter
extends BaseFragmentShader {

	public static ThresholdFilter instance;
	
	public ThresholdFilter() {
		super("haxademic/shaders/filters/threshold.glsl");
		setCutoff(0.5f);
		setCrossfade(1f);
	}
	
	public static ThresholdFilter instance() {
		if(instance != null) return instance;
		instance = new ThresholdFilter();
		return instance;
	}

	public void setCutoff(float cutoff) {
		shader.set("cutoff", cutoff);
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
