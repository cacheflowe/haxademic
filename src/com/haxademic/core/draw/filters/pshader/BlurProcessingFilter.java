package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;

import processing.core.PApplet;
import processing.core.PGraphics;

public class BlurProcessingFilter
extends BaseFragmentShader {

	public static BlurProcessingFilter instance;
	
	public BlurProcessingFilter(PApplet p) {
		super(p, "haxademic/shaders/filters/blur-processing.glsl");
		setBlurSize(2);
		setSigma(2f);
	}
	
	public static BlurProcessingFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new BlurProcessingFilter(p);
		return instance;
	}
	
	public void applyTo(PGraphics pg) {
		shader.set("horizontalPass", 0);
		pg.filter(shader);
		shader.set("horizontalPass", 1);
		pg.filter(shader);
	}
	
	public void applyTo(PApplet p) {
		shader.set("horizontalPass", 0);
		p.filter(shader);
		shader.set("horizontalPass", 1);
		p.filter(shader);
	}

	//	uniform int blurSize;       
	//	uniform int horizontalPass; // 0 or 1 to indicate vertical or horizontal pass
	//	uniform float sigma;        // The sigma value for the gaussian function: higher value means more blur

	public void setBlurSize(int blurSize) {
		shader.set("blurSize", blurSize);
	}
	
	public void setSigma(float sigma) {
		shader.set("sigma", sigma);
	}
	
}
