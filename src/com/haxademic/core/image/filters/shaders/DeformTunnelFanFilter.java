package com.haxademic.core.image.filters.shaders;

import processing.core.PApplet;

public class DeformTunnelFanFilter
extends BaseFilter {

	public static DeformTunnelFanFilter instance;
	
	public DeformTunnelFanFilter(PApplet p) {
		super(p, "shaders/filters/deform-tunnel-fan.glsl");
	}
	
	public static DeformTunnelFanFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new DeformTunnelFanFilter(p);
		return instance;
	}

}
