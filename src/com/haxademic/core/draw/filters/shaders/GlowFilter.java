package com.haxademic.core.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PApplet;
import processing.core.PGraphics;

public class GlowFilter
extends BaseFilter {

	public static GlowFilter instance;
	
	public GlowFilter(PApplet p) {
		super(p, "shaders/filters/glow.glsl");
		setSize(20f);
		setRadialSamples(64f);
		setSampleStep(1f);
		setGlowColor(0f, 0f, 0f, 0.5f);
	}
	
	public static GlowFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new GlowFilter(p);
		return instance;
	}
	
	public void setSize(float size) {
		shader.set("sampleDistance", size);
	}
	
	public void setRadialSamples(float radialSamples) {
		shader.set("radialSamples", radialSamples);
	}
	
	public void setSampleStep(float sampleStep) {
		shader.set("sampleStep", sampleStep);
	}
	
	public void setGlowColor(float r, float g, float b, float a) {
		shader.set("glowColor", r, g, b, a);
	}
	
	public void setReplaceOriginal(boolean replace) {
		int replaceInt = (replace == true) ? 1 : 0;
		shader.set("replaceOriginal", replaceInt);
	}
	
	public PGraphics getShadowBuffer(PGraphics buffer, int blurSteps) {
		PGraphics shadowCopy = ImageUtil.imageToGraphics(buffer);
		setReplaceOriginal(true);
		shadowCopy.filter(shader);
		// additional blur for smoothness
		for (int i = 0; i < blurSteps; i++) {
			BlurProcessingFilter.instance(P.p).applyTo(shadowCopy);
		}
		// reset shader
		setReplaceOriginal(false);
		return shadowCopy;
	}
}
