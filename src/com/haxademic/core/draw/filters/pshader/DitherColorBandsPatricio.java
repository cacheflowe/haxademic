package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.render.FrameLoop;

import processing.core.PImage;

public class DitherColorBandsPatricio
extends BaseFragmentShader {

	public static DitherColorBandsPatricio instance;
	
	public DitherColorBandsPatricio() {
		super("haxademic/shaders/filters/dither-color-bands-patricio.glsl");
		setTime(FrameLoop.count(0.00000001f));
		setBlueNoiseTex(ImageCacher.get("haxademic/images/noise/blue-noise-512.png"));
	}
	
	public static DitherColorBandsPatricio instance() {
		if(instance != null) return instance;
		instance = new DitherColorBandsPatricio();
		return instance;
	}
	
	public void setBlueNoiseTex(PImage blueNoiseTex) {
		shader.set("blueNoiseTex", blueNoiseTex);
	}
	
	public void setAmp(float ditherAmp) {
		shader.set("ditherAmp", ditherAmp);
	}
	
}
