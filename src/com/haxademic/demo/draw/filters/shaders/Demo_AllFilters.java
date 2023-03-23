package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.PG;
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
import com.haxademic.core.draw.filters.pshader.BrightnessClampFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessRemapFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessToAlphaFilter;
import com.haxademic.core.draw.filters.pshader.BulgeLinearFilter;
import com.haxademic.core.draw.filters.pshader.ChromaColorFilter;
import com.haxademic.core.draw.filters.pshader.ColorCorrectionFilter;
import com.haxademic.core.draw.filters.pshader.ColorDistortionFilter;
import com.haxademic.core.draw.filters.pshader.ColorRotateFilter;
import com.haxademic.core.draw.filters.pshader.ColorSolidFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeFromTexture;
import com.haxademic.core.draw.filters.pshader.ColorizeTwoColorsFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.pshader.CubicLensDistortionFilterOscillate;
import com.haxademic.core.draw.filters.pshader.DenoiseSmartFilter;
import com.haxademic.core.draw.filters.pshader.DilateFilter;
import com.haxademic.core.draw.filters.pshader.DisplacementMapFilter;
import com.haxademic.core.draw.filters.pshader.DitherFilter;
import com.haxademic.core.draw.filters.pshader.EdgeColorDarkenFilter;
import com.haxademic.core.draw.filters.pshader.EdgeColorFadeFilter;
import com.haxademic.core.draw.filters.pshader.EdgesFilter;
import com.haxademic.core.draw.filters.pshader.EmbossFilter;
import com.haxademic.core.draw.filters.pshader.ErosionFilter;
import com.haxademic.core.draw.filters.pshader.FXAAFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackMapFilter;
import com.haxademic.core.draw.filters.pshader.FlipHFilter;
import com.haxademic.core.draw.filters.pshader.FlipVFilter;
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
import com.haxademic.core.draw.filters.pshader.RadialFlareFilter;
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
import com.haxademic.core.draw.image.ImageUtil;
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
//		pg = PG.newPG32(pg.width, pg.height, false, false);	// causes problems with certain shaders
		
		noiseTexture = new TextureShader(TextureShader.noise_simplex_2d_iq);
		noiseBuffer = PG.newPG(p.width, p.height);
		
		filters = new BaseFragmentShader[] {
//				PixelateHexFilter.instance(),
//	            EdgeColorFadeFilter.instance(),
			AlphaStepFilter.instance(),
			BadTVGlitchFilter.instance(),
			BadTVLinesFilter.instance(),
			BlendTowardsTexture.instance(),
			BloomFilter.instance(),
			BlurBasicFilter.instance(),
			BlurHFilter.instance(),
			BlurHMapFilter.instance(),
			BlurProcessingFilter.instance(),
			BrightnessClampFilter.instance(),
			BrightnessFilter.instance(),
			BrightnessRemapFilter.instance(),
			BrightnessStepFilter.instance(),
			BrightnessToAlphaFilter.instance(),
			BulgeLinearFilter.instance(),
			ChromaColorFilter.instance(),
			ColorCorrectionFilter.instance(),
			ColorDistortionFilter.instance(),
			ColorizeFilter.instance(),
			ColorizeFromTexture.instance(),
			ColorizeTwoColorsFilter.instance(),
			ColorRotateFilter.instance(),
			ColorSolidFilter.instance(),
			ContrastFilter.instance(),
			CubicLensDistortionFilter.instance(),
			CubicLensDistortionFilterOscillate.instance(),
//			DeformBloomFilter.instance(),
//			DeformTunnelFanFilter.instance(),
			DenoiseSmartFilter.instance(),
			DilateFilter.instance(),
			DisplacementMapFilter.instance(),
			DitherFilter.instance(),
			EdgeColorDarkenFilter.instance(),
			EdgeColorFadeFilter.instance(),
			EdgesFilter.instance(),
			EmbossFilter.instance(),
			ErosionFilter.instance(),
			FakeLightingFilter.instance(),
			FeedbackMapFilter.instance(),
			FlipHFilter.instance(),
			FlipVFilter.instance(),
			FXAAFilter.instance(),
			GlitchImageGlitcherFilter.instance(),
			GlitchPseudoPixelSortingFilter.instance(),
			GlitchShaderAFilter.instance(),
			GlitchShakeFilter.instance(),
			GlowFilter.instance(),
			GodRays.instance(),
			GradientCoverWipe.instance(),
			GrainFilter.instance(),
			HalftoneCamoFilter.instance(),
			HalftoneFilter.instance(),
			HalftoneLinesFilter.instance(),
			HueFilter.instance(),
			InvertFilter.instance(),
			LeaveBlackFilter.instance(),
			LeaveWhiteFilter.instance(),
			LiquidWarpFilter.instance(),
			LumaColorReplaceFilter.instance(),
			KaleidoFilter.instance(),
			MaskThreeTextureFilter.instance(),
			MirrorQuadFilter.instance(),
			PixelateFilter.instance(),
			Pixelate2Filter.instance(),
			RadialBlurFilter.instance(),
			RadialFlareFilter.instance(),
			RadialRipplesFilter.instance(),
			ReflectFilter.instance(),
			RepeatFilter.instance(),
			RotateFilter.instance(),
			SaturateHSVFilter.instance(),
			SaturationFilter.instance(),
			SharpenFilter.instance(),
			SharpenMapFilter.instance(),
			SphereDistortionFilter.instance(),
			ThresholdFilter.instance(),
			VignetteAltFilter.instance(),
			VignetteFilter.instance(),
			WarperFilter.instance(),
			WobbleFilter.instance(),
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
		DebugView.setValue("filterIndex", filterIndex);

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
		ColorizeFromTexture.instance().setTexture(ImageGradient.PASTELS());
		ColorizeFromTexture.instance().setLumaMult(false);
		ColorizeFromTexture.instance().setCrossfade(1f);

		
		// apply active filter
		BaseFragmentShader curFilter = filters[filterIndex];
		if(curFilter == AlphaStepFilter.instance()) {
			AlphaStepFilter.instance().setAlphaStep(P.map(Mouse.yNorm, 0, 1, -1f, 1f));
			AlphaStepFilter.instance().applyTo(pg);
		} else if(curFilter == BadTVGlitchFilter.instance()) {
			BadTVGlitchFilter.instance().setTime(p.frameCount * 0.01f);
			BadTVGlitchFilter.instance().applyTo(pg);
		} else if(curFilter == BadTVLinesFilter.instance()) {
			BadTVLinesFilter.instance().setTime(p.frameCount * 0.01f);
			BadTVLinesFilter.instance().setGrayscale(0);
			BadTVLinesFilter.instance().setIntensityN(Mouse.xNorm);
			BadTVLinesFilter.instance().setIntensityS(Mouse.yNorm);
			BadTVLinesFilter.instance().setCountS(4096.0f);
			BadTVLinesFilter.instance().applyTo(pg);
		} else if(curFilter == BlendTowardsTexture.instance()) {
			BlendTowardsTexture.instance().setBlendLerp(Mouse.xNorm);
			BlendTowardsTexture.instance().setSourceTexture(noiseBuffer);
			BlendTowardsTexture.instance().applyTo(pg);
		} else if(curFilter == BloomFilter.instance()) {
			BloomFilter.instance().setStrength(Mouse.xNorm * 2f);
			BloomFilter.instance().setBlurIterations(P.round(Mouse.yNorm * 10f));
			BloomFilter.instance().setBlendMode(P.round(p.frameCount / 100f) % 3);
			BloomFilter.instance().applyTo(pg);
		} else if(curFilter == BlurBasicFilter.instance()) {
			BlurBasicFilter.instance().applyTo(pg);
		} else if(curFilter == BlurHFilter.instance()) {
			BlurHFilter.instance().setBlurByPercent(Mouse.xNorm * 2f, p.width);
			BlurHFilter.instance().applyTo(pg);
			BlurVFilter.instance().setBlurByPercent(Mouse.yNorm * 2f, p.height);
			BlurVFilter.instance().applyTo(pg);
		} else if(curFilter == BlurHMapFilter.instance()) {
			BlurHMapFilter.instance().setMap(noiseBuffer);
			BlurHMapFilter.instance().setAmpMin(0);
			BlurHMapFilter.instance().setAmpMax(Mouse.xNorm * 10f);
			BlurHMapFilter.instance().applyTo(pg);
			BlurVMapFilter.instance().setMap(noiseBuffer);
			BlurVMapFilter.instance().setAmpMin(0);
			BlurVMapFilter.instance().setAmpMax(Mouse.yNorm * 10f);
			BlurVMapFilter.instance().applyTo(pg);
		} else if(curFilter == BlurProcessingFilter.instance()) {
			BlurProcessingFilter.instance().setBlurSize(P.round(Mouse.yNorm * 10f));
			BlurProcessingFilter.instance().setSigma(Mouse.xNorm * 10f);
			BlurProcessingFilter.instance().applyTo(pg);
		} else if(curFilter == BrightnessClampFilter.instance()) {
			BrightnessClampFilter.instance().setLow(Mouse.xNorm);
			BrightnessClampFilter.instance().setHigh(Mouse.yNorm);
			BrightnessClampFilter.instance().applyTo(pg);
		} else if(curFilter == BrightnessFilter.instance()) {
			BrightnessFilter.instance().setBrightness(Mouse.yNorm * 10f);
			BrightnessFilter.instance().applyTo(pg);
		} else if(curFilter == BrightnessRemapFilter.instance()) {
		    BrightnessRemapFilter.instance().setLow(Mouse.xNorm);
		    BrightnessRemapFilter.instance().setHigh(Mouse.yNorm);
		    BrightnessRemapFilter.instance().applyTo(pg);
		} else if(curFilter == BrightnessStepFilter.instance()) {
			BrightnessStepFilter.instance().setBrightnessStep(P.map(Mouse.yNorm, 0, 1, -1f, 1f));
			BrightnessStepFilter.instance().applyTo(pg);
		} else if(curFilter == BrightnessToAlphaFilter.instance()) {
			BrightnessToAlphaFilter.instance().setFlip(Mouse.xNorm > 0.5f);
			BrightnessToAlphaFilter.instance().applyTo(pg);
		} else if(curFilter == BulgeLinearFilter.instance()) {
			BulgeLinearFilter.instance().setControlX(Mouse.xNorm);
			BulgeLinearFilter.instance().setMixAmp(Mouse.yNorm);
			BulgeLinearFilter.instance().setGainCurve(Mouse.xNorm * 2f);
			BulgeLinearFilter.instance().setDebug(Mouse.xNorm > 0.9f);
			BulgeLinearFilter.instance().applyTo(pg);
		} else if(curFilter == ChromaColorFilter.instance()) {
			ChromaColorFilter.instance().presetBlackKnockout();
			ChromaColorFilter.instance().setThresholdSensitivity(Mouse.xNorm);
			ChromaColorFilter.instance().setSmoothing(Mouse.yNorm);
			ChromaColorFilter.instance().setColorToReplace(0, 0, 0);
			ChromaColorFilter.instance().applyTo(pg);
		} else if(curFilter == ColorCorrectionFilter.instance()) {
			ColorCorrectionFilter.instance().setContrast(Mouse.xNorm * 10f);
			ColorCorrectionFilter.instance().setGamma(Mouse.yNorm * 10f);
			ColorCorrectionFilter.instance().applyTo(pg);
		} else if(curFilter == ColorDistortionFilter.instance()) {
			ColorDistortionFilter.instance().setAmplitude(Mouse.xNorm * 2f);
			ColorDistortionFilter.instance().setTime(p.frameCount * 0.05f * Mouse.yNorm);
			ColorDistortionFilter.instance().applyTo(pg);
		} else if(curFilter == ColorizeFilter.instance()) {
			ColorizeFilter.instance().setTargetR(Mouse.xNorm);
			ColorizeFilter.instance().setTargetG(Mouse.yNorm);
			ColorizeFilter.instance().setTargetB(Mouse.xNorm);
			ColorizeFilter.instance().setPosterSteps(Mouse.yNorm * 20f);
			ColorizeFilter.instance().applyTo(pg);
		} else if(curFilter == ColorizeFromTexture.instance()) {
			ColorizeFromTexture.instance().setTexture(ImageGradient.PASTELS());
			ColorizeFromTexture.instance().setLumaMult(Mouse.xNorm > 0.5f);
			ColorizeFromTexture.instance().setCrossfade(Mouse.yNorm);
			ColorizeFromTexture.instance().applyTo(pg);
		} else if(curFilter == ColorizeTwoColorsFilter.instance()) {
			ColorizeTwoColorsFilter.instance().setColor1(1f, 0f, 1f);
			ColorizeTwoColorsFilter.instance().setColor2(0f, 1f, 1f);
			ColorizeTwoColorsFilter.instance().setCrossfadeMode(P.round(Mouse.xNorm * 2));
			ColorizeTwoColorsFilter.instance().applyTo(pg);
		} else if(curFilter == ColorRotateFilter.instance()) {
			ImageUtil.cropFillCopyImage(DemoAssets.justin(), pg, true);	// needs color to operate on
			ColorRotateFilter.instance().setRotate(Mouse.xNorm);
			ColorRotateFilter.instance().setCrossfade(Mouse.yNorm);
			ColorRotateFilter.instance().applyTo(pg);
		} else if(curFilter == ColorSolidFilter.instance()) {
			ColorSolidFilter.instance().setColor(Mouse.xNorm, Mouse.yNorm, Mouse.xNorm, Mouse.yNorm);
			ColorSolidFilter.instance().setCrossfade(Mouse.yNorm);
			ColorSolidFilter.instance().applyTo(pg);
		} else if(curFilter == ContrastFilter.instance()) {
			ContrastFilter.instance().setContrast(Mouse.xNorm * 3);
			ContrastFilter.instance().applyTo(pg);
		} else if(curFilter == CubicLensDistortionFilter.instance()) {
	 		CubicLensDistortionFilter.instance().setAmplitude(P.map(p.mouseX, 0, p.width, -20f, 20f));
			CubicLensDistortionFilter.instance().setSeparation(P.map(p.mouseY, 0, p.height, 0, 3f));
			CubicLensDistortionFilter.instance().applyTo(pg);
		} else if(curFilter == CubicLensDistortionFilterOscillate.instance()) {
			// old distortion
			CubicLensDistortionFilterOscillate.instance().setTime(p.frameCount * 0.01f);
			CubicLensDistortionFilterOscillate.instance().applyTo(pg);
//		} else if(curFilter == DeformBloomFilter.instance()) {
//			DeformBloomFilter.instance().applyTo(pg);
		} else if(curFilter == DenoiseSmartFilter.instance()) {
		    DenoiseSmartFilter.instance().setSigma(P.map(p.mouseX, 0, p.width, 0, 20f));
		    DenoiseSmartFilter.instance().setKSigma(P.map(p.mouseY, 0, p.height, 0, 10f));
		    DenoiseSmartFilter.instance().setThreshold(P.map(p.mouseY, 0, p.height, 0, 1f));
		    DenoiseSmartFilter.instance().applyTo(pg);
		} else if(curFilter == DilateFilter.instance()) {
			DilateFilter.instance().applyTo(pg);
		} else if(curFilter == DisplacementMapFilter.instance()) {
			DisplacementMapFilter.instance().setMap(noiseBuffer);
			DisplacementMapFilter.instance().setMode(P.floor(Mouse.xNorm * 7.99f));
			DisplacementMapFilter.instance().setAmp(Mouse.yNorm * 0.5f);
			DisplacementMapFilter.instance().setDivider(11 - Mouse.yNorm * 10f);
			DisplacementMapFilter.instance().applyTo(pg);
		} else if(curFilter == DitherFilter.instance()) {
			if(Mouse.xNorm < 0.33f) DitherFilter.instance().setDitherMode2x2();
			else if(Mouse.xNorm < 0.66f) DitherFilter.instance().setDitherMode4x4();
			else DitherFilter.instance().setDitherMode8x8();
			DitherFilter.instance().applyTo(pg);
		} else if(curFilter == EdgeColorDarkenFilter.instance()) {
			EdgeColorDarkenFilter.instance().setSpreadX(Mouse.xNorm);
			EdgeColorDarkenFilter.instance().setSpreadY(Mouse.yNorm);
			EdgeColorDarkenFilter.instance().applyTo(pg);
		} else if(curFilter == EdgeColorFadeFilter.instance()) {
			EdgeColorFadeFilter.instance().setEdgeColor(1f, 0f, 0f);
			EdgeColorFadeFilter.instance().setSpreadX(Mouse.xNorm);
			EdgeColorFadeFilter.instance().setSpreadY(Mouse.yNorm);
			EdgeColorFadeFilter.instance().setCrossfade(0.5f + Mouse.yNorm);
			EdgeColorFadeFilter.instance().applyTo(pg);
		} else if(curFilter == EdgesFilter.instance()) {
			EdgesFilter.instance().applyTo(pg);
		} else if(curFilter == EmbossFilter.instance()) {
			EmbossFilter.instance().applyTo(pg);
		} else if(curFilter == ErosionFilter.instance()) {
			ErosionFilter.instance().applyTo(pg);
		} else if(curFilter == FakeLightingFilter.instance()) {
			FakeLightingFilter.instance().setAmbient(Mouse.xNorm * 4f);
			FakeLightingFilter.instance().setGradAmp(Mouse.xNorm * 4f);
			FakeLightingFilter.instance().setGradBlur(Mouse.yNorm * 2f);
			FakeLightingFilter.instance().setSpecAmp(Mouse.yNorm * 1.5f);
			FakeLightingFilter.instance().setDiffDark(Mouse.yNorm * 3f);
			FakeLightingFilter.instance().setMap(pg);
			FakeLightingFilter.instance().applyTo(pg);
		} else if(curFilter == FeedbackMapFilter.instance()) {
			FeedbackMapFilter.instance().setMap(noiseBuffer);
			FeedbackMapFilter.instance().setAmp(Mouse.yNorm * 0.5f);
			FeedbackMapFilter.instance().setBrightnessStep(P.map(Mouse.xNorm, 0, 1, -1f, 1f));
			FeedbackMapFilter.instance().setAlphaStep(P.map(Mouse.yNorm, 0, 1, -1f, 1f));
			FeedbackMapFilter.instance().applyTo(pg);
		} else if(curFilter == FlipHFilter.instance()) {
			FlipHFilter.instance().applyTo(pg);
		} else if(curFilter == FlipVFilter.instance()) {
			FlipVFilter.instance().applyTo(pg);
		} else if(curFilter == FXAAFilter.instance()) {
			FXAAFilter.instance().applyTo(pg);
		} else if(curFilter == GlitchImageGlitcherFilter.instance()) {
			GlitchImageGlitcherFilter.instance().setTime(p.frameCount * 0.01f);
			GlitchImageGlitcherFilter.instance().setAmp(Mouse.xNorm);
			GlitchImageGlitcherFilter.instance().setCrossfade(Mouse.yNorm);
			GlitchImageGlitcherFilter.instance().setColorSeparation((Mouse.yNorm > 0.5f));
			GlitchImageGlitcherFilter.instance().setBarSize(Mouse.yNorm);
			GlitchImageGlitcherFilter.instance().setGlitchSpeed(Mouse.xNorm);
			GlitchImageGlitcherFilter.instance().setNumSlices(Mouse.yNorm * 200f);
			GlitchImageGlitcherFilter.instance().applyTo(pg);
		} else if(curFilter == GlitchPseudoPixelSortingFilter.instance()) {
			// GlitchPseudoPixelSortingFilter.instance().setThresholdLow(Mouse.xNorm);
			// GlitchPseudoPixelSortingFilter.instance().setThresholdHigh(Mouse.yNorm);
			GlitchPseudoPixelSortingFilter.instance().setThresholdThresholdsCurved(Mouse.xNorm);
			GlitchPseudoPixelSortingFilter.instance().applyTo(pg);
		} else if(curFilter == GlitchShaderAFilter.instance()) {
			GlitchShaderAFilter.instance().setAmp(Mouse.xNorm * 2f);
			GlitchShaderAFilter.instance().setCrossfade(Mouse.yNorm);
			GlitchShaderAFilter.instance().applyTo(pg);
		} else if(curFilter == GlitchShakeFilter.instance()) {
			GlitchShakeFilter.instance().setTime(p.frameCount * 0.01f);
			GlitchShakeFilter.instance().setGlitchSpeed(Mouse.xNorm);
			GlitchShakeFilter.instance().setAmp(Mouse.xNorm * 2f);
			GlitchShakeFilter.instance().setCrossfade(Mouse.yNorm);
			GlitchShakeFilter.instance().setSubdivide1(Mouse.xNorm * 256f);
			GlitchShakeFilter.instance().setSubdivide2(Mouse.yNorm * 256f);
			GlitchShakeFilter.instance().applyTo(pg);
		} else if(curFilter == GlowFilter.instance()) {
			LeaveBlackFilter.instance().setCrossfade(1f);
			LeaveBlackFilter.instance().applyTo(pg);

			GlowFilter.instance().setSize(100f * Mouse.xNorm);
			GlowFilter.instance().setRadialSamples(16f);
			GlowFilter.instance().setGlowColor(0f, 0f, 0f, 1f);
			GlowFilter.instance().applyTo(pg);
		} else if(curFilter == GodRays.instance()) {
			GodRays.instance().setDecay(Mouse.xNorm);
			GodRays.instance().setWeight(Mouse.yNorm);
			GodRays.instance().setRotation(oscillate());
			GodRays.instance().setAmp(0.5f + 0.5f * oscillate());
			GodRays.instance().applyTo(pg);
		} else if(curFilter == GradientCoverWipe.instance()) {
			GradientCoverWipe.instance().setColorTop(1f, 0f, 1f, 1f);
			GradientCoverWipe.instance().setColorBot(0f, 1f, 1f, 1f);
			GradientCoverWipe.instance().setProgress(Mouse.yNorm);
			GradientCoverWipe.instance().setGradientEdge(Mouse.xNorm);
			GradientCoverWipe.instance().applyTo(pg);
		} else if(curFilter == GrainFilter.instance()) {
			GrainFilter.instance().setTime(p.frameCount * 0.01f * Mouse.yNorm * 10f);
			GrainFilter.instance().setCrossfade(Mouse.xNorm);
			GrainFilter.instance().applyTo(pg);
		} else if(curFilter == HalftoneFilter.instance()) {
			float halftoneSize = Mouse.xNorm * 1024f;
			HalftoneFilter.instance().setAngle(Mouse.xNorm * P.TWO_PI);
			HalftoneFilter.instance().setScale(Mouse.yNorm * 3f);
			HalftoneFilter.instance().setSizeT(halftoneSize, halftoneSize);
			HalftoneFilter.instance().setCenter(halftoneSize/2f, halftoneSize/2f);
			HalftoneFilter.instance().applyTo(pg);
		} else if(curFilter == HalftoneCamoFilter.instance()) {
			HalftoneCamoFilter.instance().setTime(p.frameCount * 0.01f * Mouse.yNorm);
			HalftoneCamoFilter.instance().setScale(Mouse.xNorm * 3f);
			HalftoneCamoFilter.instance().applyTo(pg);
		} else if(curFilter == HalftoneLinesFilter.instance()) {
//			setSampleDistX(200f);   // divisions for kernel sampling (width)
//			setSampleDistY(80f);	// divisions for kernel sampling (height)
			HalftoneLinesFilter.instance().setRows(Mouse.yNorm * 150f);
			HalftoneLinesFilter.instance().setRotation(Mouse.xNorm * P.TWO_PI);
//			setRotation(0f);
//			setAntiAlias(0.1f);
//			setMode(3);
			HalftoneLinesFilter.instance().applyTo(pg);
		} else if(curFilter == HueFilter.instance()) {
			ColorizeFromTexture.instance().applyTo(pg);
			
			HueFilter.instance().setHue(Mouse.xNorm * 360f);
			HueFilter.instance().applyTo(pg);
		} else if(curFilter == InvertFilter.instance()) {
			InvertFilter.instance().applyTo(pg);
		} else if(curFilter == KaleidoFilter.instance()) {
			KaleidoFilter.instance().setAngle(Mouse.xNorm * P.TWO_PI);
			KaleidoFilter.instance().setSides(Mouse.yNorm * 16f);
			KaleidoFilter.instance().applyTo(pg);
		} else if(curFilter == LeaveBlackFilter.instance()) {
			LeaveBlackFilter.instance().setCrossfade(Mouse.xNorm);
			LeaveBlackFilter.instance().applyTo(pg);
		} else if(curFilter == LeaveWhiteFilter.instance()) {
			LeaveWhiteFilter.instance().setCrossfade(Mouse.xNorm);
			LeaveWhiteFilter.instance().applyTo(pg);
		} else if(curFilter == LiquidWarpFilter.instance()) {
//			setAmplitude(0.02f);
//			setFrequency(6.0f);
			LiquidWarpFilter.instance().setTime(p.frameCount * 0.01f);
			LiquidWarpFilter.instance().setAmplitude(Mouse.xNorm * 0.1f);
			LiquidWarpFilter.instance().setFrequency(Mouse.yNorm * 20f);
			LiquidWarpFilter.instance().applyTo(pg);
		} else if(curFilter == LumaColorReplaceFilter.instance()) {
			LumaColorReplaceFilter.instance().setTargetColor(Mouse.xNorm, 1f, 1f, Mouse.yNorm);
			LumaColorReplaceFilter.instance().setDiffRange(Mouse.xNorm);
			LumaColorReplaceFilter.instance().setLumaTarget(Mouse.yNorm);
			LumaColorReplaceFilter.instance().applyTo(pg);
		} else if(curFilter == MaskThreeTextureFilter.instance()) {
			ThresholdFilter.instance().setCutoff(Mouse.xNorm);
			ThresholdFilter.instance().setCrossfade(Mouse.yNorm);
			ThresholdFilter.instance().applyTo(pg);

			MaskThreeTextureFilter.instance().setMask(pg);
			MaskThreeTextureFilter.instance().setTexture1(DemoAssets.justin());
			MaskThreeTextureFilter.instance().setTexture2(DemoAssets.textureNebula());
			MaskThreeTextureFilter.instance().applyTo(pg);
		} else if(curFilter == MirrorQuadFilter.instance()) {
			MirrorQuadFilter.instance().setZoom(Mouse.yNorm * 5f);
			MirrorQuadFilter.instance().applyTo(pg);
		} else if(curFilter == PixelateFilter.instance()) {
			PixelateFilter.instance().setDivider(Mouse.xNorm * 100f, p.width, p.height);
			PixelateFilter.instance().applyTo(pg);
		} else if(curFilter == PixelateHexFilter.instance()) {
			PixelateHexFilter.instance().setDivider(5f + 45f * Mouse.xNorm);
			PixelateHexFilter.instance().applyTo(pg);
		} else if(curFilter == Pixelate2Filter.instance()) {
			Pixelate2Filter.instance().setDivider(Mouse.xNorm * 10f);
			Pixelate2Filter.instance().applyTo	(pg);
		} else if(curFilter == RadialFlareFilter.instance()) {
			RadialFlareFilter.instance().setImageBrightness(0f + Mouse.yNorm * 10f);
			RadialFlareFilter.instance().setFlareBrightness(0f + Mouse.yNorm * 10f);
			RadialFlareFilter.instance().setRadialLength(0.5f + Mouse.xNorm * 0.5f);
			RadialFlareFilter.instance().setIters(100f + Mouse.xNorm * 3000f);
			RadialFlareFilter.instance().applyTo(pg);
		} else if(curFilter == RadialBlurFilter.instance()) {
			RadialBlurFilter.instance().applyTo(pg);
		} else if(curFilter == RadialRipplesFilter.instance()) {
			RadialRipplesFilter.instance().setTime(p.frameCount * 0.01f);
			RadialRipplesFilter.instance().setAmplitude(Mouse.xNorm * 4f);
			RadialRipplesFilter.instance().applyTo(pg);
		} else if(curFilter == ReflectFilter.instance()) {
			ReflectFilter.instance().setHorizontal(Mouse.xNorm < 0.5f);
			ReflectFilter.instance().setReflectPosition(Mouse.yNorm);
			ReflectFilter.instance().applyTo(pg);
		} else if(curFilter == RepeatFilter.instance()) {
			RepeatFilter.instance().setZoom(Mouse.yNorm * 15f);
			RepeatFilter.instance().applyTo(pg);
		} else if(curFilter == RotateFilter.instance()) {
			RotateFilter.instance().setRotation(Mouse.xNorm * 2f * P.TWO_PI);
			RotateFilter.instance().setZoom(Mouse.yNorm * 15f);
			RotateFilter.instance().applyTo(pg);
		} else if(curFilter == SaturateHSVFilter.instance()) {
			ColorizeFromTexture.instance().applyTo(pg);
			
			SaturateHSVFilter.instance().setSaturation(Mouse.xNorm * 10f);
			SaturateHSVFilter.instance().applyTo(pg);
		} else if(curFilter == SaturationFilter.instance()) {
			ColorizeFromTexture.instance().applyTo(pg);
			
			SaturationFilter.instance().setSaturation(Mouse.xNorm * 10f);
			SaturationFilter.instance().applyTo(pg);
		} else if(curFilter == SharpenFilter.instance()) {
			SharpenFilter.instance().setSharpness(Mouse.xNorm * 10f);
			SharpenFilter.instance().applyTo(pg);
		} else if(curFilter == SharpenMapFilter.instance()) {
			SharpenMapFilter.instance().setMap(noiseBuffer);
			SharpenMapFilter.instance().setAmpMax(Mouse.xNorm * 10f);
			SharpenMapFilter.instance().setAmpMin(Mouse.yNorm * 10f);
			SharpenMapFilter.instance().applyTo(pg);
		} else if(curFilter == SphereDistortionFilter.instance()) {
			SphereDistortionFilter.instance().setAmplitude(P.map(p.mouseX, 0, p.width, -20f, 20f));
			SphereDistortionFilter.instance().applyTo(pg);
		} else if(curFilter == ThresholdFilter.instance()) {
			ThresholdFilter.instance().setCutoff(Mouse.xNorm);
			ThresholdFilter.instance().setCrossfade(Mouse.yNorm);
			ThresholdFilter.instance().applyTo(pg);
		} else if(curFilter == VignetteAltFilter.instance()) {
			VignetteAltFilter.instance().setDarkness(-5f + 10f * Mouse.xNorm);
			VignetteAltFilter.instance().setSpread(Mouse.yNorm * 5f);
			VignetteAltFilter.instance().applyTo(pg);
		} else if(curFilter == VignetteFilter.instance()) {
			VignetteFilter.instance().setDarkness(-5f + 10f * Mouse.xNorm);
			VignetteFilter.instance().setSpread(Mouse.yNorm * 5f);
			VignetteFilter.instance().applyTo(pg);
		} else if(curFilter == WarperFilter.instance()) {
			WarperFilter.instance().applyTo(pg);
		} else if(curFilter == WobbleFilter.instance()) {
//			setSpeed(1f);
//			setStrength(0.001f);
//			setSize(100f);
			WobbleFilter.instance().setTime(p.frameCount * 0.01f);
			WobbleFilter.instance().setSpeed(2f); // Mouse.xNorm * 3f);
			WobbleFilter.instance().setStrength(Mouse.xNorm);
			WobbleFilter.instance().setSize(Mouse.yNorm * 5f);
			WobbleFilter.instance().applyTo(pg);
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

//	public void keyPressed() {
//		super.keyPressed();
//		int numEffects = filters.length;
//		if(p.key == '1') filterIndex = (filterIndex > 0) ? filterIndex - 1 : numEffects - 1;
//		if(p.key == '2') filterIndex = (filterIndex < numEffects - 1) ? filterIndex + 1 : 0;
//		P.out("filterIndex", filterIndex);
//	}

}
