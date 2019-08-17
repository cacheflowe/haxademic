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
		BadTV2,
		Grain,
		Slide,
		ColorSweep,
	};
	
	// funky enum randomization helper
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
		setModeConfig();
		
		// change glitchProgress increment for different speeds
		glitchProgress.setInc(MathUtil.randRangeDecimal(0.007f, 0.03f));
	}
	
	protected void setModeConfig() {
		switch (glitchMode) {
			case Pixelate2:
				// Pixelate2Filter.instance(P.p).applyTo(buffer);
				break;
	
			case Shake:
				GlitchShakeFilter.instance(P.p).setGlitchSpeed(MathUtil.randRangeDecimal(0.25f, 0.65f));
				GlitchShakeFilter.instance(P.p).setSubdivide1(64f);
				GlitchShakeFilter.instance(P.p).setSubdivide2(64f);
				break;
				
			case ImageGlitcher:
				GlitchImageGlitcherFilter.instance(P.p).setColorSeparation(MathUtil.randBoolean());
				GlitchImageGlitcherFilter.instance(P.p).setBarSize(MathUtil.randRangeDecimal(0.25f, 0.75f));
				GlitchImageGlitcherFilter.instance(P.p).setGlitchSpeed(MathUtil.randRangeDecimal(0.25f, 0.75f));
				GlitchImageGlitcherFilter.instance(P.p).setNumSlices(MathUtil.randRangeDecimal(10, 30));
				break;
				
			case Repeat:
				// RepeatFilter.instance(P.p).applyTo(buffer);
				break;
	
			case Mirror:
				ReflectFilter.instance(P.p).setHorizontal(MathUtil.randBoolean());
				ReflectFilter.instance(P.p).setReflectPosition(MathUtil.randBoolean() ? 0.5f : MathUtil.randRangeDecimal(0.2f, 0.8f));
				break;
			
			case BadTV2:
				BadTVLinesFilter.instance(P.p).setGrayscale(0);
				BadTVLinesFilter.instance(P.p).setCountS(4096.0f / 4f);
				break;
				
			case ColorSweep:
				LumaColorReplaceFilter.instance(P.p).setTargetColor(MathUtil.randRangeDecimal(0, 1), MathUtil.randRangeDecimal(0, 1), MathUtil.randRangeDecimal(0, 1), 1f);
				LumaColorReplaceFilter.instance(P.p).setDiffRange(MathUtil.randRangeDecimal(0.05f, 0.2f));
				break;
				
			default:
				break;
		}
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
				GlitchShakeFilter.instance(P.p).setAmp(progressInverse);
				GlitchShakeFilter.instance(P.p).setCrossfade(1f);
				GlitchShakeFilter.instance(P.p).applyTo(buffer);
				break;
				
			case ImageGlitcher:
				GlitchImageGlitcherFilter.instance(P.p).setTime(shaderTimeStepped);
				GlitchImageGlitcherFilter.instance(P.p).setAmp(progressInverse);
				GlitchImageGlitcherFilter.instance(P.p).setCrossfade(1f);
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
				ColorDistortionFilter.instance(P.p).setAmplitude(progressInverse * 4f);
				ColorDistortionFilter.instance(P.p).setTime(shaderTime * 1f);
				ColorDistortionFilter.instance(P.p).applyTo(buffer);
				break;
				
			case BadTV2:
				BadTVLinesFilter.instance(P.p).setTime(shaderTime);
				BadTVLinesFilter.instance(P.p).setIntensityN(progressInverse);
				BadTVLinesFilter.instance(P.p).setIntensityS(progressInverse);
				BadTVLinesFilter.instance(P.p).applyTo(buffer);
				break;
				
			case Grain:
				GrainFilter.instance(P.p).setTime(shaderTime);
				GrainFilter.instance(P.p).setCrossfade(progressInverse * 0.5f);
				GrainFilter.instance(P.p).applyTo(buffer);
				break;
				
			case Slide:
				float xSlide = progressInverse * (-1f + 2f * P.p.noise(P.p.frameCount * 0.002f));
				float ySlide = progressInverse * (-1f + 2f * P.p.noise((P.p.frameCount + 10000) * 0.002f));
				RepeatFilter.instance(P.p).setOffset(xSlide, ySlide);
				RepeatFilter.instance(P.p).applyTo(buffer);
				break;
				
			case ColorSweep:
				// LumaColorReplaceFilter.instance(P.p).setLumaTarget(3f * progressInverse - 1f);	// slide from 2 -> -1
				LumaColorReplaceFilter.instance(P.p).setLumaTarget(progressInverse);
				LumaColorReplaceFilter.instance(P.p).applyTo(buffer);
				break;
				
			default:
				// P.out("No glitch filter selected!");
				break;
		}
	}
	
}
