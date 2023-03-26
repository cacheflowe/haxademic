package com.haxademic.core.draw.filters.pshader.compound;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;

import processing.core.PGraphics;

public class ReactionDiffusionStepFilter {

	public ReactionDiffusionStepFilter() {}
	
	public static void applyTo(PGraphics pg, int iterations, int blurIterations, float blurH, float blurV, float sharpen) {
		applyTo(pg, iterations, blurIterations, blurH, blurV, sharpen, false, 0.5f, 0.5f);
	}
	
	public static void applyTo(PGraphics pg, int iterations, int blurIterations, float blurH, float blurV, float sharpen, boolean thresholdActive, float threshCrossfade, float threshCutoff) {
		// blur then sharpen. since blur has limits before banding, 
		// it has its own iteration count
		for (int i = 0; i < iterations; i++) {

			BlurHFilter.instance().setBlurByPercent(blurH, pg.width);
			BlurVFilter.instance().setBlurByPercent(blurV, pg.height);
			for (int j = 0; j < blurIterations; j++) {
				BlurHFilter.instance().applyTo(pg);
				BlurVFilter.instance().applyTo(pg);
			}
			
			SharpenFilter.instance().setSharpness(sharpen);
			SharpenFilter.instance().applyTo(pg);
		}
		
		// thresh if needed
		if(thresholdActive) {
			ThresholdFilter.instance().setCrossfade(threshCrossfade);
			ThresholdFilter.instance().setCutoff(threshCutoff);
			ThresholdFilter.instance().applyTo(pg);
		}

		// fully desaturate to make sure we're grayscale 
		SaturationFilter.instance().setSaturation(0);
		SaturationFilter.instance().applyTo(pg);
	}
	
}
