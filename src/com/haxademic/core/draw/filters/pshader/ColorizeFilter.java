package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

public class ColorizeFilter
extends BaseFragmentShader {

	public static ColorizeFilter instance;
	
	public ColorizeFilter() {
		super("haxademic/shaders/filters/colorize.glsl");
		setTargetR(1f);
		setTargetG(1f);
		setTargetB(1f);
		setPosterSteps(1f);
	}
	
	public static ColorizeFilter instance() {
		if(instance != null) return instance;
		instance = new ColorizeFilter();
		return instance;
	}
	
	public void setTargetR(float targetR) {
		shader.set("targetR", targetR);
	}
	
	public void setTargetG(float targetG) {
		shader.set("targetG", targetG);
	}

	public void setTargetB(float targetB) {
		shader.set("targetB", targetB);
	}

	public void setPosterSteps(float posterSteps) {
		shader.set("posterSteps", posterSteps);
	}
	
}
