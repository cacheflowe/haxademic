package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class ReflectFilter
extends BaseFragmentShader {

	public static ReflectFilter instance;
	
	public ReflectFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/reflect.glsl");
		setHorizontal(true);
		setReflectPosition(0.5f);
	}
	
	public static ReflectFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ReflectFilter(p);
		return instance;
	}

	public void setHorizontal(boolean horizontal) {
		shader.set("horizontal", horizontal);
	}
	
	public void setReflectPosition(float reflectPosition) {
		shader.set("reflectPosition", reflectPosition);
	}

}
