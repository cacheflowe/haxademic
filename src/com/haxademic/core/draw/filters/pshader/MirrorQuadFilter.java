package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;
import processing.core.PGraphics;

public class MirrorQuadFilter
extends BaseFragmentShader {

	public static MirrorQuadFilter instance;
	
	public MirrorQuadFilter() {
		super("haxademic/shaders/filters/mirror-quad.glsl");
		setZoom(1f);
	}
	
	public static MirrorQuadFilter instance() {
		if(instance != null) return instance;
		instance = new MirrorQuadFilter();
		return instance;
	}
	
	public void applyTo(PGraphics pg) {
		shader.set("textureDupe", pg);
		super.applyTo(pg);
	}
	
	public void applyTo(PApplet p) {
		shader.set("textureDupe", p.g);
		super.applyTo(p);
	}
	
	public void setZoom(float zoom) {
		shader.set("zoom", zoom);
	}

}
