package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class DeformTunnelFanFilter
extends BaseFragmentShader {

	public static DeformTunnelFanFilter instance;
	
	public DeformTunnelFanFilter() {
		super("haxademic/shaders/filters/deform-tunnel-fan.glsl");
	}
	
	public static DeformTunnelFanFilter instance() {
		if(instance != null) return instance;
		instance = new DeformTunnelFanFilter();
		return instance;
	}

}
