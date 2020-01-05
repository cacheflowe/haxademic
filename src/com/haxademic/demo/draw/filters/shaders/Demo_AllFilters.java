package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.filters.pshader.AlphaStepFilter;
import com.haxademic.core.draw.filters.pshader.BadTVGlitchFilter;
import com.haxademic.core.draw.filters.pshader.BadTVLinesFilter;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BloomFilter;
import com.haxademic.core.draw.filters.pshader.BlurBasicFilter;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurHMapFilter;
import com.haxademic.core.draw.filters.pshader.BlurProcessingFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BlurVMapFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessToAlphaFilter;
import com.haxademic.core.draw.filters.pshader.BulgeLinearFilter;
import com.haxademic.core.draw.filters.pshader.ChromaColorFilter;
import com.haxademic.core.draw.filters.pshader.ColorCorrectionFilter;
import com.haxademic.core.draw.filters.pshader.ColorDistortionFilter;
import com.haxademic.core.draw.filters.pshader.ColorSolidFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.ColorizeTwoColorsFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.pshader.CubicLensDistortionFilterOscillate;
import com.haxademic.core.draw.filters.pshader.DilateFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.filters.pshader.EdgeColorDarkenFilter;
import com.haxademic.core.draw.filters.pshader.EdgeColorFadeFilter;
import com.haxademic.core.draw.filters.pshader.EdgesFilter;
import com.haxademic.core.draw.filters.pshader.EmbossFilter;
import com.haxademic.core.draw.filters.pshader.ErosionFilter;
import com.haxademic.core.draw.filters.pshader.FXAAFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackMapFilter;
import com.haxademic.core.draw.filters.pshader.GlitchImageGlitcherFilter;
import com.haxademic.core.draw.filters.pshader.GlitchPseudoPixelSortingFilter;
import com.haxademic.core.draw.filters.pshader.GlitchShaderAFilter;
import com.haxademic.core.draw.filters.pshader.GlitchShakeFilter;
import com.haxademic.core.draw.filters.pshader.GlowFilter;
import com.haxademic.core.draw.filters.pshader.GodRays;
import com.haxademic.core.draw.filters.pshader.GradientCoverWipe;
import com.haxademic.core.draw.filters.pshader.GrainFilter;
import com.haxademic.core.draw.filters.pshader.HalftoneCamoFilter;
import com.haxademic.core.draw.filters.pshader.HalftoneFilter;
import com.haxademic.core.draw.filters.pshader.HalftoneLinesFilter;
import com.haxademic.core.draw.filters.pshader.HueFilter;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.KaleidoFilter;
import com.haxademic.core.draw.filters.pshader.LeaveBlackFilter;
import com.haxademic.core.draw.filters.pshader.LeaveWhiteFilter;
import com.haxademic.core.draw.filters.pshader.LiquidWarpFilter;
import com.haxademic.core.draw.filters.pshader.LumaColorReplaceFilter;
import com.haxademic.core.draw.filters.pshader.MaskThreeTextureFilter;
import com.haxademic.core.draw.filters.pshader.MirrorQuadFilter;
import com.haxademic.core.draw.filters.pshader.Pixelate2Filter;
import com.haxademic.core.draw.filters.pshader.PixelateFilter;
import com.haxademic.core.draw.filters.pshader.PixelateHexFilter;
import com.haxademic.core.draw.filters.pshader.RadialBlurFilter;
import com.haxademic.core.draw.filters.pshader.RadialRipplesFilter;
import com.haxademic.core.draw.filters.pshader.ReflectFilter;
import com.haxademic.core.draw.filters.pshader.RepeatFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.SaturateHSVFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;
import com.haxademic.core.draw.filters.pshader.SharpenMapFilter;
import com.haxademic.core.draw.filters.pshader.SphereDistortionFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.filters.pshader.VignetteAltFilter;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.filters.pshader.WarperFilter;
import com.haxademic.core.draw.filters.pshader.WobbleFilter;
import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.shared.InputTrigger;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Demo_AllFilters
extends PAppletHax { public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO: 
	// - Add commented-out filters
	// - Draw to off-screen buffer so the transparency-based filters work: (ChromaColor/ChromaKey/LeaveBleck/LeaveWhite/Glow)
	// - Grab previous frame for BlendTowardsTexture
	// - Add PrefsSliders for shaders with 3+ params


	protected TextureShader texture;
	protected PGraphics textureBuffer;
	protected PShader customShader;

	protected TextureShader noiseTexture;
	protected PGraphics noiseBuffer;
	

	protected InputTrigger triggerPrev = new InputTrigger().addKeyCodes(new char[]{'1'});
	protected InputTrigger triggerNext = new InputTrigger().addKeyCodes(new char[]{'2'});
	protected InputTrigger triggerToggle = new InputTrigger().addKeyCodes(new char[]{' '});
	
	protected BaseFragmentShader[] filters;
	protected int filterIndex = 0;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 400 );
	}

	protected void firstFrame() {
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq);
		noiseBuffer = p.createGraphics(p.width, p.height, PRenderers.P2D);
		
		filters = new BaseFragmentShader[] {
//				PixelateHexFilter.instance(p),
			AlphaStepFilter.instance(p),
			BadTVGlitchFilter.instance(p),
			BadTVLinesFilter.instance(p),
			BlendTowardsTexture.instance(p),
			BloomFilter.instance(p),
			BlurBasicFilter.instance(p),
			BlurHFilter.instance(p),
			BlurHMapFilter.instance(p),
			BlurProcessingFilter.instance(p),
			BrightnessFilter.instance(p),
			BrightnessStepFilter.instance(p),
			BrightnessToAlphaFilter.instance(p),
			BulgeLinearFilter.instance(p),
			ChromaColorFilter.instance(p),
			ColorCorrectionFilter.instance(p),
			ColorDistortionFilter.instance(p),
			ColorizeFilter.instance(p),
			ColorizeFromTexture.instance(p),
			ColorizeTwoColorsFilter.instance(p),
			ColorSolidFilter.instance(p),
			ContrastFilter.instance(p),
			CubicLensDistortionFilter.instance(p),
			CubicLensDistortionFilterOscillate.instance(p),
//			DeformBloomFilter.instance(p),
//			DeformTunnelFanFilter.instance(p),
			DilateFilter.instance(p),
			DisplacementMapFilter.instance(p),
			EdgeColorDarkenFilter.instance(p),
			EdgeColorFadeFilter.instance(p),
			EdgesFilter.instance(p),
			EmbossFilter.instance(p),
			ErosionFilter.instance(p),
			FakeLightingFilter.instance(p),
			FeedbackMapFilter.instance(p),
			FXAAFilter.instance(p),
			GlitchImageGlitcherFilter.instance(p),
			GlitchPseudoPixelSortingFilter.instance(p),
			GlitchShaderAFilter.instance(p),
			GlitchShakeFilter.instance(p),
			GlowFilter.instance(p),
			GodRays.instance(p),
			GradientCoverWipe.instance(p),
			GrainFilter.instance(p),
			HalftoneCamoFilter.instance(p),
			HalftoneFilter.instance(p),
			HalftoneLinesFilter.instance(p),
			HueFilter.instance(p),
			InvertFilter.instance(p),
			LeaveBlackFilter.instance(p),
			LeaveWhiteFilter.instance(p),
			LiquidWarpFilter.instance(p),
			LumaColorReplaceFilter.instance(p),
			KaleidoFilter.instance(p),
			MaskThreeTextureFilter.instance(p),
			MirrorQuadFilter.instance(p),
			PixelateFilter.instance(p),
			Pixelate2Filter.instance(p),
			RadialBlurFilter.instance(p),
			RadialRipplesFilter.instance(p),
			ReflectFilter.instance(p),
			RepeatFilter.instance(p),
			RotateFilter.instance(p),
			SaturateHSVFilter.instance(p),
			SaturationFilter.instance(p),
			SharpenFilter.instance(p),
			SharpenMapFilter.instance(p),
			SphereDistortionFilter.instance(p),
			ThresholdFilter.instance(p),
			VignetteAltFilter.instance(p),
			VignetteFilter.instance(p),
			WarperFilter.instance(p),
			WobbleFilter.instance(p),
		};

		textureBuffer = p.createGraphics(pg.width, pg.height, PRenderers.P3D);
		texture = new TextureShader(TextureShader.bw_clouds);
//		texture = new TextureShader(TextureShader.bw_radial_stripes);
		
		customShader = p.loadShader(FileUtil.getPath("haxademic/shaders/filters/luma-color-replace.glsl"));
//		customShader = p.loadShader(FileUtil.getFile("haxademic/shaders/filters/repeat.glsl"));
	}
	
	protected float oscillate() {
		return P.sin(p.frameCount * 0.01f);
	}
	
	protected void drawApp() {
		// show red background for transparency-relevant shaders
		p.background(255, 0, 0);
		p.noStroke();
		
		// cycle through effects
		int numEffects = filters.length;
		if(triggerPrev.triggered()) filterIndex = (filterIndex > 0) ? filterIndex - 1 : numEffects - 1;
		if(triggerNext.triggered()) filterIndex = (filterIndex < numEffects - 1) ? filterIndex + 1 : 0;

		// debug log mouse position
		DebugView.setValue("Mouse.xNorm", Mouse.xNorm);
		DebugView.setValue("Mouse.yNorm", Mouse.yNorm);
		String filterName = "";
		
		// secondary noise
		noiseTexture.updateTime();
		noiseTexture.shader().set("offset", 0f, p.frameCount * 0.01f);
		noiseTexture.shader().set("rotation", 0f, p.frameCount * 0.01f);
		noiseTexture.shader().set("zoom", 1f);
		noiseBuffer.filter(noiseTexture.shader());
		
		// update cur shader as image processing basis
		texture.updateTime();
		textureBuffer.filter(texture.shader());
		DebugView.setTexture("textureBuffer", textureBuffer);
		
		// draw to main offscreen buffer
		pg.beginDraw();
//		pg.clear();
		pg.background(0);
		pg.fill(255);
		pg.image(textureBuffer, 0, 0);
		
		// draw some text to make sure we know shader orientation (i.e., doesn't flip y axis)
		pg.fill(127 + 127f * P.sin(p.frameCount * 0.01f));
		pg.textFont(DemoAssets.fontBitlow(100));
		pg.textAlign(P.CENTER, P.CENTER);
		pg.text("FILTER", 0, 0, pg.width, pg.height);
		pg.endDraw();
		
		// helper pre=processing shaders
		ColorizeFromTexture.instance(p).setTexture(ImageGradient.PASTELS());
		ColorizeFromTexture.instance(p).setLumaMult(false);
		ColorizeFromTexture.instance(p).setCrossfade(1f);

		
		// apply active filter
		BaseFragmentShader curFilter = filters[filterIndex];
		if(curFilter == AlphaStepFilter.instance(p)) {
			AlphaStepFilter.instance(p).setAlphaStep(P.map(Mouse.yNorm, 0, 1, -1f, 1f));
			AlphaStepFilter.instance(p).applyTo(pg);
		} else if(curFilter == BadTVGlitchFilter.instance(p)) {
			BadTVGlitchFilter.instance(p).setTime(p.frameCount * 0.01f);
			BadTVGlitchFilter.instance(p).applyTo(pg);
		} else if(curFilter == BadTVLinesFilter.instance(p)) {
			BadTVLinesFilter.instance(p).setTime(p.frameCount * 0.01f);
			BadTVLinesFilter.instance(p).setGrayscale(0);
			BadTVLinesFilter.instance(p).setIntensityN(Mouse.xNorm);
			BadTVLinesFilter.instance(p).setIntensityS(Mouse.yNorm);
			BadTVLinesFilter.instance(p).setCountS(4096.0f);
			BadTVLinesFilter.instance(p).applyTo(pg);
		} else if(curFilter == BlendTowardsTexture.instance(p)) {
			BlendTowardsTexture.instance(p).setBlendLerp(Mouse.xNorm);
			BlendTowardsTexture.instance(p).setSourceTexture(noiseBuffer);
			BlendTowardsTexture.instance(p).applyTo(pg);
		} else if(curFilter == BloomFilter.instance(p)) {
			BloomFilter.instance(p).setStrength(Mouse.xNorm * 2f);
			BloomFilter.instance(p).setBlurIterations(P.round(Mouse.yNorm * 10f));
			BloomFilter.instance(p).setBlendMode(P.round(p.frameCount / 100f) % 3);
			BloomFilter.instance(p).applyTo(pg);
		} else if(curFilter == BlurBasicFilter.instance(p)) {
			BlurBasicFilter.instance(p).applyTo(pg);
		} else if(curFilter == BlurHFilter.instance(p)) {
			BlurHFilter.instance(p).setBlurByPercent(Mouse.xNorm * 2f, p.width);
			BlurHFilter.instance(p).applyTo(pg);
			BlurVFilter.instance(p).setBlurByPercent(Mouse.yNorm * 2f, p.height);
			BlurVFilter.instance(p).applyTo(pg);
		} else if(curFilter == BlurHMapFilter.instance(p)) {
			BlurHMapFilter.instance(p).setMap(noiseBuffer);
			BlurHMapFilter.instance(p).setBlurByPercent(Mouse.xNorm * 2f, p.width);
			BlurHMapFilter.instance(p).applyTo(pg);
			BlurVMapFilter.instance(p).setMap(noiseBuffer);
			BlurVMapFilter.instance(p).setBlurByPercent(Mouse.yNorm * 2f, p.height);
			BlurVMapFilter.instance(p).applyTo(pg);
		} else if(curFilter == BlurProcessingFilter.instance(p)) {
			BlurProcessingFilter.instance(p).setBlurSize(P.round(Mouse.yNorm * 10f));
			BlurProcessingFilter.instance(p).setSigma(Mouse.xNorm * 10f);
			BlurProcessingFilter.instance(p).applyTo(pg);
		} else if(curFilter == BrightnessFilter.instance(p)) {
			BrightnessFilter.instance(p).setBrightness(Mouse.yNorm * 10f);
			BrightnessFilter.instance(p).applyTo(pg);
		} else if(curFilter == BrightnessStepFilter.instance(p)) {
			BrightnessStepFilter.instance(p).setBrightnessStep(P.map(Mouse.yNorm, 0, 1, -1f, 1f));
			BrightnessStepFilter.instance(p).applyTo(pg);
		} else if(curFilter == BrightnessToAlphaFilter.instance(p)) {
			BrightnessToAlphaFilter.instance(p).setFlip(Mouse.xNorm > 0.5f);
			BrightnessToAlphaFilter.instance(p).applyTo(pg);
		} else if(curFilter == BulgeLinearFilter.instance(p)) {
			BulgeLinearFilter.instance(p).setControlX(Mouse.xNorm);
			BulgeLinearFilter.instance(p).setMixAmp(Mouse.yNorm);
			BulgeLinearFilter.instance(p).setGainCurve(Mouse.xNorm * 2f);
			BulgeLinearFilter.instance(p).setDebug(Mouse.xNorm > 0.9f);
			BulgeLinearFilter.instance(p).applyTo(pg);
		} else if(curFilter == ChromaColorFilter.instance(p)) {
			ChromaColorFilter.instance(p).presetBlackKnockout();
			ChromaColorFilter.instance(p).setThresholdSensitivity(Mouse.xNorm);
			ChromaColorFilter.instance(p).setSmoothing(Mouse.yNorm);
			ChromaColorFilter.instance(p).setColorToReplace(0, 0, 0);
			ChromaColorFilter.instance(p).applyTo(pg);
		} else if(curFilter == ColorCorrectionFilter.instance(p)) {
			ColorCorrectionFilter.instance(p).setContrast(Mouse.xNorm * 10f);
			ColorCorrectionFilter.instance(p).setGamma(Mouse.yNorm * 10f);
			ColorCorrectionFilter.instance(p).applyTo(pg);
		} else if(curFilter == ColorDistortionFilter.instance(p)) {
			ColorDistortionFilter.instance(p).setAmplitude(Mouse.xNorm * 2f);
			ColorDistortionFilter.instance(p).setTime(p.frameCount * 0.05f * Mouse.yNorm);
			ColorDistortionFilter.instance(p).applyTo(pg);
		} else if(curFilter == ColorizeFilter.instance(p)) {
			ColorizeFilter.instance(p).setTargetR(Mouse.xNorm);
			ColorizeFilter.instance(p).setTargetG(Mouse.yNorm);
			ColorizeFilter.instance(p).setTargetB(Mouse.xNorm);
			ColorizeFilter.instance(p).setPosterSteps(Mouse.yNorm * 20f);
			ColorizeFilter.instance(p).applyTo(pg);
		} else if(curFilter == ColorizeFromTexture.instance(p)) {
			ColorizeFromTexture.instance(p).setTexture(ImageGradient.PASTELS());
			ColorizeFromTexture.instance(p).setLumaMult(Mouse.xNorm > 0.5f);
			ColorizeFromTexture.instance(p).setCrossfade(Mouse.yNorm);
			ColorizeFromTexture.instance(p).applyTo(pg);
		} else if(curFilter == ColorizeTwoColorsFilter.instance(p)) {
			ColorizeTwoColorsFilter.instance(p).setColor1(1f, 0f, 1f);
			ColorizeTwoColorsFilter.instance(p).setColor2(0f, 1f, 1f);
			ColorizeTwoColorsFilter.instance(p).setCrossfadeMode(P.round(Mouse.xNorm * 2));
			ColorizeTwoColorsFilter.instance(p).applyTo(pg);
		} else if(curFilter == ColorSolidFilter.instance(p)) {
			ColorSolidFilter.instance(p).setColor(Mouse.xNorm, Mouse.yNorm, Mouse.xNorm, Mouse.yNorm);
			ColorSolidFilter.instance(p).setCrossfade(Mouse.yNorm);
			ColorSolidFilter.instance(p).applyTo(pg);
		} else if(curFilter == ContrastFilter.instance(p)) {
			ContrastFilter.instance(p).setContrast(Mouse.xNorm * 3);
			ContrastFilter.instance(p).applyTo(pg);
		} else if(curFilter == CubicLensDistortionFilter.instance(p)) {
	 		CubicLensDistortionFilter.instance(p).setAmplitude(P.map(p.mouseX, 0, p.width, -20f, 20f));
			CubicLensDistortionFilter.instance(p).setSeparation(P.map(p.mouseY, 0, p.height, 0, 3f));
			CubicLensDistortionFilter.instance(p).applyTo(pg);
		} else if(curFilter == CubicLensDistortionFilterOscillate.instance(p)) {
			// old distortion
			CubicLensDistortionFilterOscillate.instance(p).setTime(p.frameCount * 0.01f);
			CubicLensDistortionFilterOscillate.instance(p).applyTo(pg);
//		} else if(curFilter == DeformBloomFilter.instance(p)) {
//			DeformBloomFilter.instance(p).applyTo(pg);
		} else if(curFilter == DilateFilter.instance(p)) {
			DilateFilter.instance(p).applyTo(pg);
		} else if(curFilter == DisplacementMapFilter.instance(p)) {
			DisplacementMapFilter.instance(p).setMap(noiseBuffer);
			DisplacementMapFilter.instance(p).setMode(P.floor(Mouse.xNorm * 7.99f));
			DisplacementMapFilter.instance(p).setAmp(Mouse.yNorm * 0.5f);
			DisplacementMapFilter.instance(p).setDivider(11 - Mouse.yNorm * 10f);
			DisplacementMapFilter.instance(p).applyTo(pg);
		} else if(curFilter == EdgeColorDarkenFilter.instance(p)) {
			EdgeColorDarkenFilter.instance(p).setSpreadX(Mouse.xNorm);
			EdgeColorDarkenFilter.instance(p).setSpreadY(Mouse.yNorm);
			EdgeColorDarkenFilter.instance(p).applyTo(pg);
		} else if(curFilter == EdgeColorFadeFilter.instance(p)) {
			EdgeColorFadeFilter.instance(p).setEdgeColor(1f, 0f, 0f);
			EdgeColorFadeFilter.instance(p).setSpreadX(Mouse.xNorm);
			EdgeColorFadeFilter.instance(p).setSpreadY(Mouse.yNorm);
			EdgeColorFadeFilter.instance(p).applyTo(pg);
		} else if(curFilter == EdgesFilter.instance(p)) {
			EdgesFilter.instance(p).applyTo(pg);
		} else if(curFilter == EmbossFilter.instance(p)) {
			EmbossFilter.instance(p).applyTo(pg);
		} else if(curFilter == ErosionFilter.instance(p)) {
			ErosionFilter.instance(p).applyTo(pg);
		} else if(curFilter == FakeLightingFilter.instance(p)) {
			FakeLightingFilter.instance(p).setAmbient(Mouse.xNorm * 4f);
			FakeLightingFilter.instance(p).setGradAmp(Mouse.xNorm * 4f);
			FakeLightingFilter.instance(p).setGradBlur(Mouse.yNorm * 2f);
			FakeLightingFilter.instance(p).setSpecAmp(Mouse.yNorm * 1.5f);
			FakeLightingFilter.instance(p).setDiffDark(Mouse.yNorm * 3f);
			FakeLightingFilter.instance(p).applyTo(pg);
		} else if(curFilter == FeedbackMapFilter.instance(p)) {
			FeedbackMapFilter.instance(p).setMap(noiseBuffer);
			FeedbackMapFilter.instance(p).setAmp(Mouse.yNorm * 0.5f);
			FeedbackMapFilter.instance(p).setBrightnessStep(P.map(Mouse.xNorm, 0, 1, -1f, 1f));
			FeedbackMapFilter.instance(p).setAlphaStep(P.map(Mouse.yNorm, 0, 1, -1f, 1f));
			FeedbackMapFilter.instance(p).applyTo(pg);
		} else if(curFilter == FXAAFilter.instance(p)) {
			FXAAFilter.instance(p).applyTo(pg);
		} else if(curFilter == GlitchImageGlitcherFilter.instance(p)) {
			GlitchImageGlitcherFilter.instance(p).setTime(p.frameCount * 0.01f);
			GlitchImageGlitcherFilter.instance(p).setAmp(Mouse.xNorm);
			GlitchImageGlitcherFilter.instance(p).setCrossfade(Mouse.yNorm);
			GlitchImageGlitcherFilter.instance(p).setColorSeparation((Mouse.yNorm > 0.5f));
			GlitchImageGlitcherFilter.instance(p).setBarSize(Mouse.yNorm);
			GlitchImageGlitcherFilter.instance(p).setGlitchSpeed(Mouse.xNorm);
			GlitchImageGlitcherFilter.instance(p).setNumSlices(Mouse.yNorm * 200f);
			GlitchImageGlitcherFilter.instance(p).applyTo(pg);
		} else if(curFilter == GlitchPseudoPixelSortingFilter.instance(p)) {
			// GlitchPseudoPixelSortingFilter.instance(p).setThresholdLow(Mouse.xNorm);
			// GlitchPseudoPixelSortingFilter.instance(p).setThresholdHigh(Mouse.yNorm);
			GlitchPseudoPixelSortingFilter.instance(p).setThresholdThresholdsCurved(Mouse.xNorm);
			GlitchPseudoPixelSortingFilter.instance(p).applyTo(pg);
		} else if(curFilter == GlitchShaderAFilter.instance(p)) {
			GlitchShaderAFilter.instance(p).setAmp(Mouse.xNorm * 2f);
			GlitchShaderAFilter.instance(p).setCrossfade(Mouse.yNorm);
			GlitchShaderAFilter.instance(p).applyTo(pg);
		} else if(curFilter == GlitchShakeFilter.instance(p)) {
			GlitchShakeFilter.instance(p).setTime(p.frameCount * 0.01f);
			GlitchShakeFilter.instance(p).setGlitchSpeed(Mouse.xNorm);
			GlitchShakeFilter.instance(p).setAmp(Mouse.xNorm * 2f);
			GlitchShakeFilter.instance(p).setCrossfade(Mouse.yNorm);
			GlitchShakeFilter.instance(p).setSubdivide1(Mouse.xNorm * 256f);
			GlitchShakeFilter.instance(p).setSubdivide2(Mouse.yNorm * 256f);
			GlitchShakeFilter.instance(p).applyTo(pg);
		} else if(curFilter == GlowFilter.instance(p)) {
			LeaveBlackFilter.instance(p).setCrossfade(1f);
			LeaveBlackFilter.instance(p).applyTo(pg);

			GlowFilter.instance(p).setSize(100f * Mouse.xNorm);
			GlowFilter.instance(p).setRadialSamples(16f);
			GlowFilter.instance(p).setGlowColor(0f, 0f, 0f, 1f);
			GlowFilter.instance(p).applyTo(pg);
		} else if(curFilter == GodRays.instance(p)) {
			GodRays.instance(p).setDecay(Mouse.xNorm);
			GodRays.instance(p).setWeight(Mouse.yNorm);
			GodRays.instance(p).setRotation(oscillate());
			GodRays.instance(p).setAmp(0.5f + 0.5f * oscillate());
			GodRays.instance(p).applyTo(pg);
		} else if(curFilter == GradientCoverWipe.instance(p)) {
			GradientCoverWipe.instance(p).setColorTop(1f, 0f, 1f, 1f);
			GradientCoverWipe.instance(p).setColorBot(0f, 1f, 1f, 1f);
			GradientCoverWipe.instance(p).setProgress(Mouse.yNorm);
			GradientCoverWipe.instance(p).setGradientEdge(Mouse.xNorm);
			GradientCoverWipe.instance(p).applyTo(pg);
		} else if(curFilter == GrainFilter.instance(p)) {
			GrainFilter.instance(p).setTime(p.frameCount * 0.01f * Mouse.yNorm * 10f);
			GrainFilter.instance(p).setCrossfade(Mouse.xNorm);
			GrainFilter.instance(p).applyTo(pg);
		} else if(curFilter == HalftoneFilter.instance(p)) {
			float halftoneSize = Mouse.xNorm * 1024f;
			HalftoneFilter.instance(p).setAngle(Mouse.xNorm * P.TWO_PI);
			HalftoneFilter.instance(p).setScale(Mouse.yNorm * 3f);
			HalftoneFilter.instance(p).setSizeT(halftoneSize, halftoneSize);
			HalftoneFilter.instance(p).setCenter(halftoneSize/2f, halftoneSize/2f);
			HalftoneFilter.instance(p).applyTo(pg);
		} else if(curFilter == HalftoneCamoFilter.instance(p)) {
			HalftoneCamoFilter.instance(p).setTime(p.frameCount * 0.01f * Mouse.yNorm);
			HalftoneCamoFilter.instance(p).setScale(Mouse.xNorm * 3f);
			HalftoneCamoFilter.instance(p).applyTo(pg);
		} else if(curFilter == HalftoneLinesFilter.instance(p)) {
//			setSampleDistX(200f);   // divisions for kernel sampling (width)
//			setSampleDistY(80f);	// divisions for kernel sampling (height)
			HalftoneLinesFilter.instance(p).setRows(Mouse.yNorm * 150f);
			HalftoneLinesFilter.instance(p).setRotation(Mouse.xNorm * P.TWO_PI);
//			setRotation(0f);
//			setAntiAlias(0.1f);
//			setMode(3);
			HalftoneLinesFilter.instance(p).applyTo(pg);
		} else if(curFilter == HueFilter.instance(p)) {
			ColorizeFromTexture.instance(p).applyTo(pg);
			
			HueFilter.instance(p).setHue(Mouse.xNorm * 360f);
			HueFilter.instance(p).applyTo(pg);
		} else if(curFilter == InvertFilter.instance(p)) {
			InvertFilter.instance(p).applyTo(pg);
		} else if(curFilter == KaleidoFilter.instance(p)) {
			KaleidoFilter.instance(p).setAngle(Mouse.xNorm * P.TWO_PI);
			KaleidoFilter.instance(p).setSides(Mouse.yNorm * 16f);
			KaleidoFilter.instance(p).applyTo(pg);
		} else if(curFilter == LeaveBlackFilter.instance(p)) {
			LeaveBlackFilter.instance(p).setCrossfade(Mouse.xNorm);
			LeaveBlackFilter.instance(p).applyTo(pg);
		} else if(curFilter == LeaveWhiteFilter.instance(p)) {
			LeaveWhiteFilter.instance(p).setCrossfade(Mouse.xNorm);
			LeaveWhiteFilter.instance(p).applyTo(pg);
		} else if(curFilter == LiquidWarpFilter.instance(p)) {
//			setAmplitude(0.02f);
//			setFrequency(6.0f);
			LiquidWarpFilter.instance(p).setTime(p.frameCount * 0.01f);
			LiquidWarpFilter.instance(p).setAmplitude(Mouse.xNorm * 0.1f);
			LiquidWarpFilter.instance(p).setFrequency(Mouse.yNorm * 20f);
			LiquidWarpFilter.instance(p).applyTo(pg);
		} else if(curFilter == LumaColorReplaceFilter.instance(p)) {
			LumaColorReplaceFilter.instance(p).setTargetColor(Mouse.xNorm, 1f, 1f, Mouse.yNorm);
			LumaColorReplaceFilter.instance(p).setDiffRange(Mouse.xNorm);
			LumaColorReplaceFilter.instance(p).setLumaTarget(Mouse.yNorm);
			LumaColorReplaceFilter.instance(p).applyTo(pg);
		} else if(curFilter == MaskThreeTextureFilter.instance(p)) {
			ThresholdFilter.instance(p).setCutoff(0.5f);
			ThresholdFilter.instance(p).applyTo(pg);

			MaskThreeTextureFilter.instance(p).setMask(pg);
			MaskThreeTextureFilter.instance(p).setTexture1(DemoAssets.justin());
			MaskThreeTextureFilter.instance(p).setTexture2(DemoAssets.textureNebula());
			MaskThreeTextureFilter.instance(p).applyTo(pg);
		} else if(curFilter == MirrorQuadFilter.instance(p)) {
			MirrorQuadFilter.instance(p).setZoom(Mouse.yNorm * 5f);
			MirrorQuadFilter.instance(p).applyTo(pg);
		} else if(curFilter == PixelateFilter.instance(p)) {
			PixelateFilter.instance(p).setDivider(Mouse.xNorm * 100f, p.width, p.height);
			PixelateFilter.instance(p).applyTo(pg);
		} else if(curFilter == PixelateHexFilter.instance(p)) {
			PixelateHexFilter.instance(p).setDivider(5f + 45f * Mouse.xNorm);
			PixelateHexFilter.instance(p).applyTo(pg);
		} else if(curFilter == Pixelate2Filter.instance(p)) {
			Pixelate2Filter.instance(p).setDivider(Mouse.xNorm * 10f);
			Pixelate2Filter.instance(p).applyTo(pg);
		} else if(curFilter == RadialBlurFilter.instance(p)) {
			RadialBlurFilter.instance(p).applyTo(pg);
		} else if(curFilter == RadialRipplesFilter.instance(p)) {
			RadialRipplesFilter.instance(p).setTime(p.frameCount * 0.01f);
			RadialRipplesFilter.instance(p).setAmplitude(Mouse.xNorm * 4f);
			RadialRipplesFilter.instance(p).applyTo(pg);
		} else if(curFilter == ReflectFilter.instance(p)) {
			ReflectFilter.instance(p).setHorizontal(Mouse.xNorm < 0.5f);
			ReflectFilter.instance(p).setReflectPosition(Mouse.yNorm);
			ReflectFilter.instance(p).applyTo(pg);
		} else if(curFilter == RepeatFilter.instance(p)) {
			RepeatFilter.instance(p).setZoom(Mouse.yNorm * 15f);
			RepeatFilter.instance(p).applyTo(pg);
		} else if(curFilter == RotateFilter.instance(p)) {
			RotateFilter.instance(p).setRotation(Mouse.xNorm * 2f * P.TWO_PI);
			RotateFilter.instance(p).setZoom(Mouse.yNorm * 15f);
			RotateFilter.instance(p).applyTo(pg);
		} else if(curFilter == SaturateHSVFilter.instance(p)) {
			ColorizeFromTexture.instance(p).applyTo(pg);
			
			SaturateHSVFilter.instance(p).setSaturation(Mouse.xNorm * 10f);
			SaturateHSVFilter.instance(p).applyTo(pg);
		} else if(curFilter == SaturationFilter.instance(p)) {
			ColorizeFromTexture.instance(p).applyTo(pg);
			
			SaturationFilter.instance(p).setSaturation(Mouse.xNorm * 10f);
			SaturationFilter.instance(p).applyTo(pg);
		} else if(curFilter == SharpenFilter.instance(p)) {
			SharpenFilter.instance(p).setSharpness(Mouse.xNorm * 10f);
			SharpenFilter.instance(p).applyTo(pg);
		} else if(curFilter == SharpenMapFilter.instance(p)) {
			SharpenMapFilter.instance(p).setMap(noiseBuffer);
			SharpenMapFilter.instance(p).setSharpnessMax(Mouse.xNorm * 10f);
			SharpenMapFilter.instance(p).setSharpnessMin(Mouse.yNorm * 10f);
			SharpenMapFilter.instance(p).applyTo(pg);
		} else if(curFilter == SphereDistortionFilter.instance(p)) {
			SphereDistortionFilter.instance(p).setAmplitude(P.map(p.mouseX, 0, p.width, -20f, 20f));
			SphereDistortionFilter.instance(p).applyTo(pg);
		} else if(curFilter == ThresholdFilter.instance(p)) {
			ThresholdFilter.instance(p).setCutoff(Mouse.xNorm);
			ThresholdFilter.instance(p).applyTo(pg);
		} else if(curFilter == VignetteAltFilter.instance(p)) {
			VignetteAltFilter.instance(p).setDarkness(-5f + 10f * Mouse.xNorm);
			VignetteAltFilter.instance(p).setSpread(Mouse.yNorm * 5f);
			VignetteAltFilter.instance(p).applyTo(pg);
		} else if(curFilter == VignetteFilter.instance(p)) {
			VignetteFilter.instance(p).setDarkness(-5f + 10f * Mouse.xNorm);
			VignetteFilter.instance(p).setSpread(Mouse.yNorm * 5f);
			VignetteFilter.instance(p).applyTo(pg);
		} else if(curFilter == WarperFilter.instance(p)) {
			WarperFilter.instance(p).applyTo(pg);
		} else if(curFilter == WobbleFilter.instance(p)) {
//			setSpeed(1f);
//			setStrength(0.001f);
//			setSize(100f);
			WobbleFilter.instance(p).setTime(p.frameCount * 0.01f);
			WobbleFilter.instance(p).setSpeed(2f); // Mouse.xNorm * 3f);
			WobbleFilter.instance(p).setStrength(Mouse.xNorm);
			WobbleFilter.instance(p).setSize(Mouse.yNorm * 5f);
			WobbleFilter.instance(p).applyTo(pg);
		} 
		
		// draw custom filter for testing
		if(customShader != null) { //  && triggerToggle.on() == false) {
//			customShader.set("test", (Mouse.xNorm > 0.5f) ? 1 : 0);
			customShader.set("diffRange", Mouse.xNorm);
			customShader.set("lumaTarget", Mouse.yNorm);
			customShader.set("targetColor", 1f, 1f, 0f, 1f);
//			pg.filter(customShader);
		}
		
		// draw offscreen buffer to app
		p.image(pg, 0, 0);
		
		// draw current filter name
		// set up context for more text
		p.fill(0, 100);
		p.rect(0, p.height - 60, p.width, 60);
		p.fill(255);
		p.textAlign(P.LEFT, P.CENTER);
		p.textFont(DemoAssets.fontRaleway(20));
		filterName = curFilter.getClass().getSimpleName();
		p.text(filterName, 20, p.height - 30);
	}

	public void keyPressed() {
		super.keyPressed();
		int numEffects = filters.length;
		if(p.key == '1') filterIndex = (filterIndex > 0) ? filterIndex - 1 : numEffects - 1;
		if(p.key == '2') filterIndex = (filterIndex < numEffects - 1) ? filterIndex + 1 : 0;
	}

}
