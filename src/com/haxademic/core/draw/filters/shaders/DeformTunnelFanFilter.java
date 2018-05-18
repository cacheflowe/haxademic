package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;

public class DeformTunnelFanFilter
extends BaseFilter {

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
