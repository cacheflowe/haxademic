package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;
import processing.core.PImage;

public class ColorizeFromTexture
extends BaseFragmentShader {

	public static ColorizeFromTexture instance;
	
	public ColorizeFromTexture(PApplet p) {
		super(p, "haxademic/shaders/filters/colorize-from-texture.glsl");
		setTexture(ImageGradient.PASTELS());
		setCrossfade(1f);
		setLumaMult(false);
	}
	
	public static ColorizeFromTexture instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ColorizeFromTexture(p);
		return instance;
	}
	
	public void setTexture(PImage texture) {
		shader.set("colorMap", texture);
	}
	
	public void setLumaMult(boolean lumaMult) {
		shader.set("lumaMult", lumaMult);
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
}
