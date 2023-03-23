package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class HalftoneFilter
extends BaseFragmentShader {

	public static HalftoneFilter instance;
	
	public HalftoneFilter() {
		super("haxademic/shaders/filters/halftone.glsl");
		setAngle(1.57f);
		setScale(1f);
		setCenter(0.5f, 0.5f);
		setSizeT(256f, 256f);
	}
	
	public static HalftoneFilter instance() {
		if(instance != null) return instance;
		instance = new HalftoneFilter();
		return instance;
	}
	
	public void setAngle(float angle) {
		shader.set("angle", angle);
	}
	
	public void setScale(float scale) {
		shader.set("scale", scale);
	}
	
	public void setCenter(float centerX, float centerY) {
		shader.set("center", centerX, centerY);
	}
	
	public void setSizeT(float tSizeX, float tSizeY) {
		shader.set("tSize", tSizeX, tSizeY);
	}
	
}
