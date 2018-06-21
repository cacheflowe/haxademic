package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;

import processing.core.PApplet;
import processing.core.PImage;

public class ColorizeFromTexture
extends BaseFilter {

	public static ColorizeFromTexture instance;
	
	public ColorizeFromTexture(PApplet p) {
		super(p, "haxademic/shaders/filters/colorize-from-texture.glsl");
		setTexture(ImageGradient.PASTELS());
	}
	
	public static ColorizeFromTexture instance(PApplet p) {
		if(instance != null) return instance;
		instance = new ColorizeFromTexture(p);
		return instance;
	}
	
	public void setTexture(PImage texture) {
		shader.set("colorMap", texture);
	}
	
}
