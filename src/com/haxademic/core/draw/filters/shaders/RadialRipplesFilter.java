package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class RadialRipplesFilter
extends BaseFilter {

	public static RadialRipplesFilter instance;
	
	public RadialRipplesFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/radial-ripples.glsl");
		setAmplitude(1f);
	}
	
	public static RadialRipplesFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new RadialRipplesFilter(p);
		return instance;
	}
	
	public void setAmplitude(float amplitude) {
		shader.set("amplitude", amplitude);
	}
	
}
