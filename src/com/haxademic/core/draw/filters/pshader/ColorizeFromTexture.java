package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PImage;

public class ColorizeFromTexture
extends BaseFragmentShader {

	public static ColorizeFromTexture instance;
	
	public ColorizeFromTexture() {
		super("haxademic/shaders/filters/colorize-from-texture.glsl");
		setTexture(ImageGradient.PASTELS());
		setCrossfade(1f);
		setOffset(0f);
		setLumaMult(false);
	}
	
	public static ColorizeFromTexture instance() {
		if(instance != null) return instance;
		instance = new ColorizeFromTexture();
		return instance;
	}
	
	public void setTexture(PImage texture) {
		shader.set("colorMap", texture);
	}
	
	public void setLumaMult(boolean lumaMult) {
		shader.set("lumaMult", (lumaMult) ? 1 : 0);
	}
	
	public void setCrossfade(float crossfade) {
		shader.set("crossfade", crossfade);
	}
	
	public void setOffset(float offset) {
		shader.set("offset", offset);
	}
	
}
