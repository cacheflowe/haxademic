package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class KaleidoFilter
extends BaseFilter {

	public static KaleidoFilter instance;
	
	public KaleidoFilter(PApplet p) {
		super(p, "shaders/filters/kaleido.glsl");
		setSides(6f);
		setAngle(0);
	}
	
	public static KaleidoFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new KaleidoFilter(p);
		return instance;
	}
	
	public void setSides(float sides) {
		shader.set("sides", sides);
	}
	
	public void setAngle(float angle) {
		shader.set("angle", angle);
	}
	
}
