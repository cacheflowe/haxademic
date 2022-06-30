package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class PinchPolesFilter
extends BaseFragmentShader {

	public static PinchPolesFilter instance;
	
	public PinchPolesFilter() {
		super(P.p, "haxademic/shaders/filters/pinch-poles.glsl");
		setCrossfade(1f);
	}
	
	public static PinchPolesFilter instance() {
		if(instance != null) return instance;
		instance = new PinchPolesFilter();
		return instance;
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
