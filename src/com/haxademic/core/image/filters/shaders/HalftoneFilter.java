package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class HalftoneFilter
extends BaseFilter {

	public static HalftoneFilter instance;
	
	public HalftoneFilter(PApplet p) {
		super(p, "shaders/filters/halftone.glsl");
		setAngle(1.57f);
		setScale(1f);
		setCenter(0.5f, 0.5f);
		setSizeT(256f, 256f);
	}
	
	public static HalftoneFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new HalftoneFilter(p);
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
