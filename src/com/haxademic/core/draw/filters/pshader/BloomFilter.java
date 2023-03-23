package com.haxademic.core.draw.filters.pshader;

import java.util.HashMap;

import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.draw.image.ImageUtil;

import processing.core.PApplet;
import processing.core.PGraphics;

public class BloomFilter
extends BaseFragmentShader {

	public static BloomFilter instance;
	
	public static HashMap<PGraphics, PGraphics> buffers = new HashMap<PGraphics, PGraphics>();
	public static float strength;
	public static int iterations;
	public static int blendMode;
	public static int BLEND_SCREEN = 0;
	public static int BLEND_MULTIPLY = 1;
	public static int BLEND_DARKEST = 2;
	
	public BloomFilter() {
		super(null);
		setStrength(1f);
		setBlurIterations(5);
		setBlendMode(BLEND_SCREEN);
	}
	
	public static BloomFilter instance() {
		if(instance != null) return instance;
		instance = new BloomFilter();
		return instance;
	}
	
	public void setStrength(float str) {
		strength = str;
	}
		
	public void setBlurIterations(int it) {
		iterations = it;
	}
	
	public void setBlendMode(int mode) {
		blendMode = mode;
	}
	
	public void applyTo(PApplet p) {
		applyTo(p.g);
	}
	
	public void applyTo(PGraphics pg) {
		// lazy-init buffer per passed-in buffer
		if(buffers.containsKey(pg) == false) {
			buffers.put(pg, PG.newPG(pg.width, pg.height));
		}
		PGraphics glowTexture = buffers.get(pg);
//		DebugView.setTexture(glowTexture);
		
		// copy image & create glow version
		glowTexture.beginDraw();
//		glowTexture.clear();
		glowTexture.background(0);
////		glowTexture.image(pg, 0, 0);
		glowTexture.endDraw();
		ImageUtil.copyImage(pg, glowTexture);
		LeaveWhiteFilter.instance().setCrossfade(0.95f);
		LeaveWhiteFilter.instance().applyTo(glowTexture);
		BlurHFilter.instance().setBlurByPercent(strength, glowTexture.width);
		BlurVFilter.instance().setBlurByPercent(strength, glowTexture.height);
		for (int i = 0; i < iterations; i++) {
			BlurHFilter.instance().applyTo(glowTexture);
			BlurVFilter.instance().applyTo(glowTexture);
		}
		
		// blend it
		if(blendMode == BLEND_SCREEN) {
			BlendTextureScreen.instance().setSourceTexture(glowTexture);
			BlendTextureScreen.instance().applyTo(pg);
		} else if(blendMode == BLEND_MULTIPLY) {
			BlendTextureMultiply.instance().setSourceTexture(glowTexture);
			BlendTextureMultiply.instance().applyTo(pg);
		} else if(blendMode == BLEND_DARKEST) {
			BlendTextureDarken.instance().setSourceTexture(glowTexture);
			BlendTextureDarken.instance().applyTo(pg);
		}
	}
}