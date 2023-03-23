package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class LeaveWhiteFilter
extends BaseFragmentShader {

	public static LeaveWhiteFilter instance;
	
	public LeaveWhiteFilter() {
		super("haxademic/shaders/filters/leave-white.glsl");
		setCrossfade(1f);
	}
	
	public static LeaveWhiteFilter instance() {
		if(instance != null) return instance;
		instance = new LeaveWhiteFilter();
		return instance;
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
