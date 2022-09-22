package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.draw.image.ImageCacher;
import com.haxademic.core.render.FrameLoop;

import processing.core.PApplet;
import processing.core.PImage;

public class DitherColorBands
extends BaseFragmentShader {

	public static DitherColorBands instance;
	
	public DitherColorBands(PApplet p) {
		super(p, "haxademic/shaders/filters/dither-color-bands.glsl");
		setTime(FrameLoop.count(0.00000001f));
		setBlueNoiseTex(ImageCacher.get("haxademic/images/noise/blue-noise-512.png"));
		setNoiseAmp(7f);
	}
	
	public static DitherColorBands instance(PApplet p) {
		if(instance != null) return instance;
		instance = new DitherColorBands(p);
		return instance;
	}
	
	public void setBlueNoiseTex(PImage blueNoiseTex) {
		shader.set("blueNoiseTex", blueNoiseTex);
	}
	
	public void setNoiseAmp(float noiseAmp) {
		shader.set("noiseAmp", noiseAmp);
	}
	
}
