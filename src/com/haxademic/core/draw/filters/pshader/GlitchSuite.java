package com.haxademic.core.draw.filters.pshader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.haxademic.core.app.P;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.LinearFloat;
import com.haxademic.core.math.easing.Penner;

import processing.core.PGraphics;

public class GlitchSuite {

	protected LinearFloat glitchProgress = new LinearFloat(0, 0.015f);
	protected GlitchMode glitchMode;	

	public enum GlitchMode {
		Pixelate2,
		ShaderA,
		PixelSorting,
		Shake,
		ImageGlitcher,
		Invert,
		HFlip,
		Edges,
		Repeat,
		Mirror,
		ColorDistortion,
//		BadTV,
		BadTV2,
		Grain,
		Slide,
//		OrangeSweep,
	};
	
	// funky enum randomization helpers
	private static final List<GlitchMode> VALUES = Collections.unmodifiableList(Arrays.asList(GlitchMode.values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();
	public static GlitchMode randomGlitchMode()  {
	  return VALUES.get(RANDOM.nextInt(SIZE));
	}
	
	// optional subset of glitch modes
	protected GlitchMode[] modes = null;
	
	public GlitchSuite() {
		this(null);
	}
	
	public GlitchSuite(GlitchMode[] modes) {
		this.modes = modes;
	}
	
	public void newGlitchMode() {
		if(this.modes == null) {
			startGlitchMode(randomGlitchMode());											// random from all mode enums
		} else {
			startGlitchMode(this.modes[MathUtil.randRange(0, modes.length - 1)]);			// random mode from user-define subset
		}
		// debug specific effect testing:
		// startGlitchMode(GlitchMode.Slide);
	}

	public void startGlitchMode(GlitchMode newGlitchMode) {
		// reset glitch progress
		glitchProgress.setCurrent(0);
		glitchProgress.setTarget(1);
		
		// select a glitch mode
		glitchMode = newGlitchMode;
		
		// TODO: effect-specific configuration
		// TODO: change glitchProgress increment for different speeds
		glitchProgress.setInc(0.02f);
	}
	
	public void applyTo(PGraphics buffer) {
		// update glitch progress
		glitchProgress.update();
		boolean isGlitching = glitchProgress.value() > 0 && glitchProgress.value() < 1;
		if(isGlitching == false) return;
		float progressInverse = 1f - glitchProgress.value();
		float shaderTime = P.p.frameCount * 0.01f;
		float shaderTimeStepped = P.floor(P.p.frameCount/5) * 0.01f;

		// apply glitchy filters to buffer 
		switch (glitchMode) {
			case Pixelate2:
				Pixelate2Filter.instance(P.p).setDivider(10f * progressInverse);
				Pixelate2Filter.instance(P.p).applyTo(buffer);
				break;

			case ShaderA:
				GlitchShaderAFilter.instance(P.p).setTime(shaderTimeStepped);
				GlitchShaderAFilter.instance(P.p).setAmp(P.constrain(progressInverse, 0.1f, 1f));
				GlitchShaderAFilter.instance(P.p).applyTo(buffer);
				break;
	
			case PixelSorting:
				GlitchPseudoPixelSortingFilter.instance(P.p).setThresholdThresholdsCurved(progressInverse);
				GlitchPseudoPixelSortingFilter.instance(P.p).applyTo(buffer);
				break;
				
			case Shake:
				GlitchShakeFilter.instance(P.p).setTime(shaderTimeStepped);
				GlitchShakeFilter.instance(P.p).setGlitchSpeed(0.4f);	 				// config?
				GlitchShakeFilter.instance(P.p).setAmp(progressInverse);
				GlitchShakeFilter.instance(P.p).setCrossfade(1f);
				GlitchShakeFilter.instance(P.p).setSubdivide1(64f);	 				// config?
				GlitchShakeFilter.instance(P.p).setSubdivide2(64f);	 				// config?
				GlitchShakeFilter.instance(P.p).applyTo(buffer);
				break;
				
			case ImageGlitcher:
				GlitchImageGlitcherFilter.instance(P.p).setTime(shaderTimeStepped);
				GlitchImageGlitcherFilter.instance(P.p).setAmp(progressInverse);
				GlitchImageGlitcherFilter.instance(P.p).setCrossfade(1f);
				GlitchImageGlitcherFilter.instance(P.p).setColorSeparation(true);
				GlitchImageGlitcherFilter.instance(P.p).setBarSize(0.5f);	 			// config?
				GlitchImageGlitcherFilter.instance(P.p).setGlitchSpeed(0.5f);			// config?
				GlitchImageGlitcherFilter.instance(P.p).setNumSlices(20f);			// config?
				GlitchImageGlitcherFilter.instance(P.p).applyTo(buffer);
				break;
				
			case Invert:
				InvertFilter.instance(P.p).applyTo(buffer);
				break;

			case HFlip:
				FlipHFilter.instance(P.p).applyTo(buffer);
				break;
				
			case Edges:
				EdgesFilter.instance(P.p).applyTo(buffer);
				break;
				
			case Repeat:
				float progressInverseEased = Penner.easeInExpo(progressInverse, 0, 1, 1);
				RepeatFilter.instance(P.p).setZoom(1f + 5f * progressInverseEased);
				RepeatFilter.instance(P.p).applyTo(buffer);
				break;

			case Mirror:
				ReflectFilter.instance(P.p).applyTo(buffer);
				break;
			
			case ColorDistortion:
				ColorDistortionFilter.instance(P.p).setAmplitude(progressInverse);
				ColorDistortionFilter.instance(P.p).setTime(shaderTime * 1f);
				ColorDistortionFilter.instance(P.p).applyTo(buffer);
				break;
				
			case BadTV2:
				BadTVLinesFilter.instance(P.p).setTime(shaderTime);
				BadTVLinesFilter.instance(P.p).setGrayscale(0);
				BadTVLinesFilter.instance(P.p).setIntensityN(progressInverse);
				BadTVLinesFilter.instance(P.p).setIntensityS(progressInverse);
				BadTVLinesFilter.instance(P.p).setCountS(4096.0f / 1f);
				BadTVLinesFilter.instance(P.p).applyTo(buffer);
				break;
				
			case Grain:
				GrainFilter.instance(P.p).setTime(shaderTime);
				GrainFilter.instance(P.p).setCrossfade(progressInverse * 0.5f);
				GrainFilter.instance(P.p).applyTo(buffer);
				break;
				
			case Slide:
				float xSlide = progressInverse * (-1f + 2f * P.p.noise(P.p.frameCount * 0.001f));
				float ySlide = progressInverse * (-1f + 2f * P.p.noise((P.p.frameCount + 10000) * 0.001f));
				RepeatFilter.instance(P.p).setOffset(xSlide, ySlide);
				RepeatFilter.instance(P.p).applyTo(buffer);
				break;
				
//			case OrangeSweep:
//				LumaColorReplaceFilter.instance(P.p).setTargetColor(1f, 0.274f, 0.023f, 1f);
//				LumaColorReplaceFilter.instance(P.p).setDiffRange(0.1f);
//				// LumaColorReplaceFilter.instance(P.p).setLumaTarget(3f * progressInverse - 1f);	// slide from 2 -> -1
//				LumaColorReplaceFilter.instance(P.p).setLumaTarget(progressInverse);
//				LumaColorReplaceFilter.instance(P.p).applyTo(buffer);
//				break;
				
			default:
				// P.out("No glitch filter selected!");
				break;
		}
	}
	
}
