package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class RadialRipplesFilter
extends BaseFragmentShader {

	public static RadialRipplesFilter instance;
	
	public RadialRipplesFilter() {
		super("haxademic/shaders/filters/radial-ripples.glsl");
		setAmplitude(1f);
	}
	
	public static RadialRipplesFilter instance() {
		if(instance != null) return instance;
		instance = new RadialRipplesFilter();
		return instance;
	}
	
	public void setAmplitude(float amplitude) {
		shader.set("amplitude", amplitude);
	}
	
}
