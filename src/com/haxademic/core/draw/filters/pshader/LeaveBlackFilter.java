package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class LeaveBlackFilter
extends BaseFragmentShader {

	public static LeaveBlackFilter instance;
	
	public LeaveBlackFilter() {
		super("haxademic/shaders/filters/leave-black.glsl");
		setCrossfade(1f);
	}
	
	public static LeaveBlackFilter instance() {
		if(instance != null) return instance;
		instance = new LeaveBlackFilter();
		return instance;
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
