package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class KaleidoFilter
extends BaseFragmentShader {

	public static KaleidoFilter instance;
	
	public KaleidoFilter() {
		super("haxademic/shaders/filters/kaleido.glsl");
		setSides(6f);
		setAngle(0);
	}
	
	public static KaleidoFilter instance() {
		if(instance != null) return instance;
		instance = new KaleidoFilter();
		return instance;
	}
	
	public void setSides(float sides) {
		shader.set("sides", sides);
	}
	
	public void setAngle(float angle) {
		shader.set("angle", angle);
	}
	
}
