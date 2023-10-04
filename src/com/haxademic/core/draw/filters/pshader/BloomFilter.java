package com.haxademic.core.draw.filters.pshader;

import java.util.HashMap;

import com.haxademic.core.data.constants.PBlendModes;
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
	public static int BLEND_ADD = 3;
	
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

	public PGraphics glowTextureFor(PGraphics pg) {
		if(buffers.containsKey(pg)) {
			return buffers.get(pg);
		} else {
			return null;
		}
	}
	
	public void applyTo(PApplet p) {
		applyTo(p);
	}
	
	public void applyTo(PGraphics pg) {
		// lazy-init buffer per passed-in buffer
		if(buffers.containsKey(pg) == false) {
			buffers.put(pg, PG.newPG(pg.width, pg.height));
		}
		PGraphics glowTexture = buffers.get(pg);
		
		// copy image & create glow version
		glowTexture.beginDraw();
		glowTexture.clear();
		glowTexture.background(255, 0);
		glowTexture.image(pg, 0, 0);
		glowTexture.endDraw();
		
		// LeaveWhiteFilter.instance().setCrossfade(0.5f);
		// LeaveWhiteFilter.instance().applyTo(glowTexture);
		
		BlurHFilter.instance().setBlurByPercent(strength, glowTexture.width);
		BlurVFilter.instance().setBlurByPercent(strength, glowTexture.height);
		for (int i = 0; i < iterations; i++) {
			BlurHFilter.instance().applyTo(glowTexture);
			BlurVFilter.instance().applyTo(glowTexture);
		}
		
		// blend it
		if(blendMode == BLEND_SCREEN) {
			pg.beginDraw();
			pg.push();
			pg.blendMode(PBlendModes.SCREEN);
			pg.image(glowTexture, 0, 0);
			pg.pop();
			pg.endDraw();
		} else if(blendMode == BLEND_ADD) {
			pg.beginDraw();
			pg.push();
			pg.blendMode(PBlendModes.ADD);
			pg.image(glowTexture, 0, 0);
			pg.pop();
			pg.endDraw();
		} else if(blendMode == BLEND_MULTIPLY) {
			BlendTextureMultiply.instance().setSourceTexture(glowTexture);
			BlendTextureMultiply.instance().applyTo(pg);
		} else if(blendMode == BLEND_DARKEST) {
			BlendTextureDarken.instance().setSourceTexture(glowTexture);
			BlendTextureDarken.instance().applyTo(pg);
		}
	}
}