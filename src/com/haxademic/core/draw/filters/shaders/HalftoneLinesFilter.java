package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class HalftoneLinesFilter
extends BaseFilter {

	public static HalftoneLinesFilter instance;
	
	public HalftoneLinesFilter(PApplet p) {
		super(p, "shaders/filters/halftone-lines.glsl");
		setSampleDistX(200f);   // divisions for kernel sampling (width)
		setSampleDistY(80f);	// divisions for kernel sampling (height)
		setRows(50f);
		setRotation(0f);
		setAntiAlias(0.1f);
		setMode(3);
	}
	
	public static HalftoneLinesFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new HalftoneLinesFilter(p);
		return instance;
	}
	
	public void setSampleDistX(float divisor) {
		shader.set("sampleDistX", divisor);
	}
	
	public void setSampleDistY(float divisor) {
		shader.set("sampleDistY", divisor);
	}
		
	public void setRows(float rows) {
		shader.set("rows", rows);
	}
	
	public void setRotation(float rotation) {
		shader.set("rotation", rotation);
	}
	
	public void setAntiAlias(float antialias) {
		shader.set("antialias", antialias);
	}
	
	public void setMode(int mode) {
		shader.set("mode", mode);
	}
	
}
