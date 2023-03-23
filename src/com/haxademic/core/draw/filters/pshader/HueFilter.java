package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class HueFilter
extends BaseFragmentShader {

	public static HueFilter instance;
	
	public HueFilter() {
		super("haxademic/shaders/filters/hue.glsl");
		setHue(90f);
	}
	
	public static HueFilter instance() {
		if(instance != null) return instance;
		instance = new HueFilter();
		return instance;
	}
	
	public void setHue(float hueAdjust) {
	    hueAdjust = hueAdjust % 360.0f * P.PI/180f;
		shader.set("hueAdjust", hueAdjust);
	}
	
}
