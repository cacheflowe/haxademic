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
				// Pixelate2Filter.instance().applyTo(buffer);
				break;
	
			case Shake:
				GlitchShakeFilter.instance().setGlitchSpeed(MathUtil.randRangeDecimal(0.25f, 0.65f));
				GlitchShakeFilter.instance().setSubdivide1(64f);
				GlitchShakeFilter.instance().setSubdivide2(64f);
				break;
				
			case ImageGlitcher:
				GlitchImageGlitcherFilter.instance().setColorSeparation(MathUtil.randBoolean());
				GlitchImageGlitcherFilter.instance().setBarSize(MathUtil.randRangeDecimal(0.25f, 0.75f));
				GlitchImageGlitcherFilter.instance().setGlitchSpeed(MathUtil.randRangeDecimal(0.25f, 0.75f));
				GlitchImageGlitcherFilter.instance().setNumSlices(MathUtil.randRangeDecimal(10, 30));
				break;
				
			case Repeat:
				// RepeatFilter.instance().applyTo(buffer);
				break;
	
			case Mirror:
				ReflectFilter.instance().setHorizontal(MathUtil.randBoolean());
				ReflectFilter.instance().setReflectPosition(MathUtil.randBoolean() ? 0.5f : MathUtil.randRangeDecimal(0.2f, 0.8f));
				break;
			
			case BadTV2:
				BadTVLinesFilter.instance().setGrayscale(0);
				BadTVLinesFilter.instance().setCountS(4096.0f / 4f);
				break;
				
			case ColorSweep:
				LumaColorReplaceFilter.instance().setTargetColor(MathUtil.randRangeDecimal(0, 1), MathUtil.randRangeDecimal(0, 1), MathUtil.randRangeDecimal(0, 1), 1f);
				LumaColorReplaceFilter.instance().setDiffRange(MathUtil.randRangeDecimal(0.05f, 0.2f));
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
				Pixelate2Filter.instance().setDivider(10f * progressInverse);
				Pixelate2Filter.instance().applyTo(buffer);
				break;

			case ShaderA:
				GlitchShaderAFilter.instance().setTime(shaderTimeStepped);
				GlitchShaderAFilter.instance().setAmp(P.constrain(progressInverse, 0.1f, 1f));
				GlitchShaderAFilter.instance().applyTo(buffer);
				break;
	
			case PixelSorting:
				GlitchPseudoPixelSortingFilter.instance().setThresholdThresholdsCurved(progressInverse);
				GlitchPseudoPixelSortingFilter.instance().applyTo(buffer);
				break;
				
			case Shake:
				GlitchShakeFilter.instance().setTime(shaderTimeStepped);
				GlitchShakeFilter.instance().setAmp(progressInverse);
				GlitchShakeFilter.instance().setCrossfade(1f);
				GlitchShakeFilter.instance().applyTo(buffer);
				break;
				
			case ImageGlitcher:
				GlitchImageGlitcherFilter.instance().setTime(shaderTimeStepped);
				GlitchImageGlitcherFilter.instance().setAmp(progressInverse);
				GlitchImageGlitcherFilter.instance().setCrossfade(1f);
				GlitchImageGlitcherFilter.instance().applyTo(buffer);
				break;
				
			case Invert:
				InvertFilter.instance().applyTo(buffer);
				break;

			case HFlip:
				FlipHFilter.instance().applyTo(buffer);
				break;
				
			case Edges:
				EdgesFilter.instance().applyTo(buffer);
				break;
				
			case Repeat:
				float progressInverseEased = Penner.easeInExpo(progressInverse);
				RepeatFilter.instance().setZoom(1f + 5f * progressInverseEased);
				RepeatFilter.instance().applyTo(buffer);
				break;

			case Mirror:
				ReflectFilter.instance().applyTo(buffer);
				break;
			
			case ColorDistortion:
				ColorDistortionFilter.instance().setAmplitude(progressInverse * 4f);
				ColorDistortionFilter.instance().setTime(shaderTime * 1f);
				ColorDistortionFilter.instance().applyTo(buffer);
				break;
				
			case BadTV2:
				BadTVLinesFilter.instance().setTime(shaderTime);
				BadTVLinesFilter.instance().setIntensityN(progressInverse);
				BadTVLinesFilter.instance().setIntensityS(progressInverse);
				BadTVLinesFilter.instance().applyTo(buffer);
				break;
				
			case Grain:
				GrainFilter.instance().setTime(shaderTime);
				GrainFilter.instance().setCrossfade(progressInverse * 0.5f);
				GrainFilter.instance().applyTo(buffer);
				break;
				
			case Slide:
				float xSlide = progressInverse * (-1f + 2f * P.p.noise(P.p.frameCount * 0.002f));
				float ySlide = progressInverse * (-1f + 2f * P.p.noise((P.p.frameCount + 10000) * 0.002f));
				RepeatFilter.instance().setOffset(xSlide, ySlide);
				RepeatFilter.instance().applyTo(buffer);
				break;
				
			case ColorSweep:
				// LumaColorReplaceFilter.instance().setLumaTarget(3f * progressInverse - 1f);	// slide from 2 -> -1
				LumaColorReplaceFilter.instance().setLumaTarget(progressInverse);
				LumaColorReplaceFilter.instance().applyTo(buffer);
				break;
				
			default:
				// P.out("No glitch filter selected!");
				break;
		}
	}
	
}
