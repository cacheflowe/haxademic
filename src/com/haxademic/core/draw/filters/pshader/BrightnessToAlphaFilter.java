package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class BrightnessToAlphaFilter
extends BaseFragmentShader {

	public static BrightnessToAlphaFilter instance;
	
	public BrightnessToAlphaFilter() {
		super("haxademic/shaders/filters/brightness-to-alpha.glsl");
		setFlip(false);
		setSmoothstepLow(0);
		setSmoothstepHigh(1);
	}
	
	public static BrightnessToAlphaFilter instance() {
		if(instance != null) return instance;
		instance = new BrightnessToAlphaFilter();
		return instance;
	}
	
	public void setFlip(boolean flip) {
		shader.set("flip", (flip == true) ? 1 : 0);
	}
	
	public void setSmoothstepLow(float smoothstepLow) {
		shader.set("smoothstepLow", smoothstepLow);
	}
	
	public void setSmoothstepHigh(float smoothstepHigh) {
		shader.set("smoothstepHigh", smoothstepHigh);
	}
		
}
