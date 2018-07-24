package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;

public class DeformTunnelFanFilter
extends BaseFragmentShader {

	public static DeformTunnelFanFilter instance;
	
	public DeformTunnelFanFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/deform-tunnel-fan.glsl");
	}
	
	public static DeformTunnelFanFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new DeformTunnelFanFilter(p);
		return instance;
	}

}
