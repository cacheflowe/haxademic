package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.filters.shaders.BadTVGlitchFilter;
import com.haxademic.core.draw.filters.shaders.BadTVLinesFilter;
import com.haxademic.core.draw.filters.shaders.BlendTowardsTexture;
import com.haxademic.core.draw.filters.shaders.BlurBasicFilter;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.filters.shaders.BlurProcessingFilter;
import com.haxademic.core.draw.filters.shaders.BlurVFilter;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;
import com.haxademic.core.draw.filters.shaders.ChromaColorFilter;
import com.haxademic.core.draw.filters.shaders.ColorCorrectionFilter;
import com.haxademic.core.draw.filters.shaders.ColorDistortionFilter;
import com.haxademic.core.draw.filters.shaders.ColorizeFilter;
import com.haxademic.core.draw.filters.shaders.ColorizeFromTexture;
import com.haxademic.core.draw.filters.shaders.ColorizeTwoColorsFilter;
import com.haxademic.core.draw.filters.shaders.ContrastFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilterOscillate;
import com.haxademic.core.draw.filters.shaders.DilateFilter;
import com.haxademic.core.draw.filters.shaders.DisplacementMapFilter;
import com.haxademic.core.draw.filters.shaders.EdgeColorDarkenFilter;
import com.haxademic.core.draw.filters.shaders.EdgeColorFadeFilter;
import com.haxademic.core.draw.filters.shaders.EdgesFilter;
import com.haxademic.core.draw.filters.shaders.EmbossFilter;
import com.haxademic.core.draw.filters.shaders.ErosionFilter;
import com.haxademic.core.draw.filters.shaders.FXAAFilter;
import com.haxademic.core.draw.filters.shaders.GlowFilter;
import com.haxademic.core.draw.filters.shaders.GodRays;
import com.haxademic.core.draw.filters.shaders.GradientCoverWipe;
import com.haxademic.core.draw.filters.shaders.HalftoneFilter;
import com.haxademic.core.draw.filters.shaders.HalftoneLinesFilter;
import com.haxademic.core.draw.filters.shaders.HueFilter;
import com.haxademic.core.draw.filters.shaders.InvertFilter;
import com.haxademic.core.draw.filters.shaders.KaleidoFilter;
import com.haxademic.core.draw.filters.shaders.LeaveBlackFilter;
import com.haxademic.core.draw.filters.shaders.LeaveWhiteFilter;
import com.haxademic.core.draw.filters.shaders.LiquidWarpFilter;
import com.haxademic.core.draw.filters.shaders.MaskThreeTextureFilter;
import com.haxademic.core.draw.filters.shaders.MirrorFilter;
import com.haxademic.core.draw.filters.shaders.PixelateFilter;
import com.haxademic.core.draw.filters.shaders.RadialBlurFilter;
import com.haxademic.core.draw.filters.shaders.RadialRipplesFilter;
import com.haxademic.core.draw.filters.shaders.RotateFilter;
import com.haxademic.core.draw.filters.shaders.SaturateHSVFilter;
import com.haxademic.core.draw.filters.shaders.SaturationFilter;
import com.haxademic.core.draw.filters.shaders.SharpenFilter;
import com.haxademic.core.draw.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.draw.filters.shaders.ThresholdFilter;
import com.haxademic.core.draw.filters.shaders.VignetteAltFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.filters.shaders.WarperFilter;
import com.haxademic.core.draw.filters.shaders.WobbleFilter;
import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;
import com.haxademic.core.draw.textures.pshader.TextureShader;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.hardware.shared.InputTrigger;

import processing.opengl.PShader;

public class Demo_AllFilters_WIP
extends PAppletHax { public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO: 
	// - Add commented-out filters
	// - Draw to off-screen buffer so the transparency-based filters work: (ChromaColor/ChromaKey/LeaveBleck/LeaveWhite/Glow)
	// - Grab previous frame for BlendTowardsTexture
	// - Add PrefsSliders for shaders with 3+ params


	protected TextureShader texture;
	protected PShader customShader;

	protected InputTrigger triggerPrev = new InputTrigger(new char[]{'1'});
	protected InputTrigger triggerNext = new InputTrigger(new char[]{'2'});
	protected InputTrigger triggerToggle = new InputTrigger(new char[]{' '});
	
	protected BaseFilter[] filters;
	protected int filterIndex = 0;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setupFirstFrame() {
		filters = new BaseFilter[] {
			BadTVGlitchFilter.instance(p),
			BadTVLinesFilter.instance(p),
			BlendTowardsTexture.instance(p),
			BlurBasicFilter.instance(p),
			BlurHFilter.instance(p),
			BlurProcessingFilter.instance(p),
			BrightnessFilter.instance(p),
			ChromaColorFilter.instance(p),
			ColorCorrectionFilter.instance(p),
			ColorDistortionFilter.instance(p),
			ColorizeFilter.instance(p),
			ColorizeFromTexture.instance(p),
			ColorizeTwoColorsFilter.instance(p),
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
			FXAAFilter.instance(p),
			GlowFilter.instance(p),
			GodRays.instance(p),
			GradientCoverWipe.instance(p),
			HalftoneFilter.instance(p),
			HalftoneLinesFilter.instance(p),
			HueFilter.instance(p),
			InvertFilter.instance(p),
			LeaveBlackFilter.instance(p),
			LeaveWhiteFilter.instance(p),
			LiquidWarpFilter.instance(p),
			KaleidoFilter.instance(p),
			MaskThreeTextureFilter.instance(p),
			MirrorFilter.instance(p),
			PixelateFilter.instance(p),
			RadialBlurFilter.instance(p),
			RadialRipplesFilter.instance(p),
			RotateFilter.instance(p),
			SaturateHSVFilter.instance(p),
			SaturationFilter.instance(p),
			SharpenFilter.instance(p),
			SphereDistortionFilter.instance(p),
			ThresholdFilter.instance(p),
			VignetteAltFilter.instance(p),
			VignetteFilter.instance(p),
			WarperFilter.instance(p),
			WobbleFilter.instance(p),
		};

		texture = new TextureShader(TextureShader.bw_clouds);
		
//		customShader = p.loadShader(FileUtil.getFile("haxademic/shaders/filters/godrays.glsl"));
	}
	
	protected float oscillate() {
		return P.sin(p.frameCount * 0.01f);
	}
	
	public void drawApp() {
		// show red background for transparency-relevant shaders
		p.background(255, 0, 0);
		p.noStroke();
		
		// cycle through effects
		int numEffects = filters.length;
		if(triggerPrev.triggered()) filterIndex = (filterIndex > 0) ? filterIndex - 1 : numEffects - 1;
		if(triggerNext.triggered()) filterIndex = (filterIndex < numEffects - 1) ? filterIndex + 1 : 0;

		// debug log mouse position
		p.debugView.setValue("p.mousePercentX()", p.mousePercentX());
		p.debugView.setValue("p.mousePercentY()", p.mousePercentY());
		String filterName = "";
		
		// update cur shader as image processing basis
		texture.updateTime();
		pg.beginDraw();
		pg.filter(texture.shader());
		
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
		BaseFilter curFilter = filters[filterIndex];
		if(curFilter == BadTVGlitchFilter.instance(p)) {
			BadTVGlitchFilter.instance(p).setTime(p.frameCount * 0.01f);
			BadTVGlitchFilter.instance(p).applyTo(pg);
		} else if(curFilter == BadTVLinesFilter.instance(p)) {
			BadTVLinesFilter.instance(p).setTime(p.frameCount * 0.01f);
			BadTVLinesFilter.instance(p).setGrayscale(0);
			BadTVLinesFilter.instance(p).setIntensityN(p.mousePercentX());
			BadTVLinesFilter.instance(p).setIntensityS(p.mousePercentY());
			BadTVLinesFilter.instance(p).setCountS(4096.0f);
			BadTVLinesFilter.instance(p).applyTo(pg);
		} else if(curFilter == BlendTowardsTexture.instance(p)) {
			BlendTowardsTexture.instance(p).setBlendLerp(p.mousePercentX());
			BlendTowardsTexture.instance(p).setSourceTexture(DemoAssets.textureJupiter());
			BlendTowardsTexture.instance(p).applyTo(pg);
		} else if(curFilter == BlurBasicFilter.instance(p)) {
			BlurBasicFilter.instance(p).applyTo(pg);
		} else if(curFilter == BlurHFilter.instance(p)) {
			BlurHFilter.instance(p).setBlurByPercent(p.mousePercentX() * 2f, p.width);
			BlurHFilter.instance(p).applyTo(pg);
			BlurVFilter.instance(p).setBlurByPercent(p.mousePercentY() * 2f, p.height);
			BlurVFilter.instance(p).applyTo(pg);
		} else if(curFilter == BlurProcessingFilter.instance(p)) {
			BlurProcessingFilter.instance(p).setBlurSize(P.round(p.mousePercentY() * 10f));
			BlurProcessingFilter.instance(p).setSigma(p.mousePercentX() * 10f);
			BlurProcessingFilter.instance(p).applyTo(pg);
		} else if(curFilter == BrightnessFilter.instance(p)) {
			BrightnessFilter.instance(p).setBrightness(p.mousePercentY() * 10f);
			BrightnessFilter.instance(p).applyTo(pg);
		} else if(curFilter == ChromaColorFilter.instance(p)) {
			ChromaColorFilter.instance(p).presetBlackKnockout();
			ChromaColorFilter.instance(p).setThresholdSensitivity(p.mousePercentX());
			ChromaColorFilter.instance(p).setSmoothing(p.mousePercentY());
			ChromaColorFilter.instance(p).setColorToReplace(0, 0, 0);
			ChromaColorFilter.instance(p).applyTo(pg);
		} else if(curFilter == ColorCorrectionFilter.instance(p)) {
			ColorCorrectionFilter.instance(p).setContrast(p.mousePercentX() * 10f);
			ColorCorrectionFilter.instance(p).setGamma(p.mousePercentY() * 10f);
			ColorCorrectionFilter.instance(p).applyTo(pg);
		} else if(curFilter == ColorDistortionFilter.instance(p)) {
			ColorDistortionFilter.instance(p).setAmplitude(p.mousePercentX() * 2f);
			ColorDistortionFilter.instance(p).setTime(p.frameCount * 0.05f * p.mousePercentY());
			ColorDistortionFilter.instance(p).applyTo(pg);
		} else if(curFilter == ColorizeFilter.instance(p)) {
			ColorizeFilter.instance(p).setTargetR(p.mousePercentX());
			ColorizeFilter.instance(p).setTargetG(p.mousePercentY());
			ColorizeFilter.instance(p).setTargetB(p.mousePercentX());
			ColorizeFilter.instance(p).applyTo(pg);
		} else if(curFilter == ColorizeFromTexture.instance(p)) {
			ColorizeFromTexture.instance(p).setTexture(ImageGradient.PASTELS());
			ColorizeFromTexture.instance(p).setLumaMult(p.mousePercentX() > 0.5f);
			ColorizeFromTexture.instance(p).setCrossfade(p.mousePercentY());
			ColorizeFromTexture.instance(p).applyTo(pg);
		} else if(curFilter == ColorizeTwoColorsFilter.instance(p)) {
			ColorizeTwoColorsFilter.instance(p).setColor1(1f, 0f, 1f);
			ColorizeTwoColorsFilter.instance(p).setColor2(0f, 1f, 1f);
			ColorizeTwoColorsFilter.instance(p).applyTo(pg);
		} else if(curFilter == ContrastFilter.instance(p)) {
			ContrastFilter.instance(p).setContrast(p.mousePercentX() * 3);
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
			DisplacementMapFilter.instance(p).setMap(DemoAssets.textureNebula());
			DisplacementMapFilter.instance(p).setMode(P.floor(p.mousePercentX() * 3f));
			DisplacementMapFilter.instance(p).applyTo(pg);
		} else if(curFilter == EdgeColorDarkenFilter.instance(p)) {
			EdgeColorDarkenFilter.instance(p).setSpreadX(p.mousePercentX());
			EdgeColorDarkenFilter.instance(p).setSpreadY(p.mousePercentY());
			EdgeColorDarkenFilter.instance(p).applyTo(pg);
		} else if(curFilter == EdgeColorFadeFilter.instance(p)) {
			EdgeColorFadeFilter.instance(p).setEdgeColor(1f, 0f, 0f);
			EdgeColorFadeFilter.instance(p).setSpreadX(p.mousePercentX());
			EdgeColorFadeFilter.instance(p).setSpreadY(p.mousePercentY());
			EdgeColorFadeFilter.instance(p).applyTo(pg);
		} else if(curFilter == EdgesFilter.instance(p)) {
			EdgesFilter.instance(p).applyTo(pg);
		} else if(curFilter == EmbossFilter.instance(p)) {
			EmbossFilter.instance(p).applyTo(pg);
		} else if(curFilter == ErosionFilter.instance(p)) {
			ErosionFilter.instance(p).applyTo(pg);
		} else if(curFilter == FXAAFilter.instance(p)) {
			FXAAFilter.instance(p).applyTo(pg);
		} else if(curFilter == GlowFilter.instance(p)) {
			LeaveBlackFilter.instance(p).setMix(1f);
			LeaveBlackFilter.instance(p).applyTo(pg);

			GlowFilter.instance(p).setSize(100f * p.mousePercentX());
			GlowFilter.instance(p).setRadialSamples(16f);
			GlowFilter.instance(p).setGlowColor(0f, 0f, 0f, 1f);
			GlowFilter.instance(p).applyTo(pg);
		} else if(curFilter == GodRays.instance(p)) {
			GodRays.instance(p).setDecay(p.mousePercentX());
			GodRays.instance(p).setWeight(p.mousePercentY());
			GodRays.instance(p).setRotation(oscillate());
			GodRays.instance(p).setAmp(0.5f + 0.5f * oscillate());
			GodRays.instance(p).applyTo(pg);
		} else if(curFilter == GradientCoverWipe.instance(p)) {
			GradientCoverWipe.instance(p).setColorTop(1f, 0f, 1f, 1f);
			GradientCoverWipe.instance(p).setColorBot(0f, 1f, 1f, 1f);
			GradientCoverWipe.instance(p).setProgress(p.mousePercentY());
			GradientCoverWipe.instance(p).applyTo(pg);
		} else if(curFilter == HalftoneFilter.instance(p)) {
			float halftoneSize = p.mousePercentX() * 1024f;
			HalftoneFilter.instance(p).setAngle(p.mousePercentX() * P.TWO_PI);
			HalftoneFilter.instance(p).setScale(p.mousePercentY() * 3f);
			HalftoneFilter.instance(p).setSizeT(halftoneSize, halftoneSize);
			HalftoneFilter.instance(p).setCenter(halftoneSize/2f, halftoneSize/2f);
			HalftoneFilter.instance(p).applyTo(pg);
		} else if(curFilter == HalftoneLinesFilter.instance(p)) {
//			setSampleDistX(200f);   // divisions for kernel sampling (width)
//			setSampleDistY(80f);	// divisions for kernel sampling (height)
			HalftoneLinesFilter.instance(p).setRows(p.mousePercentY() * 150f);
			HalftoneLinesFilter.instance(p).setRotation(p.mousePercentX() * P.TWO_PI);
//			setRotation(0f);
//			setAntiAlias(0.1f);
//			setMode(3);
			HalftoneLinesFilter.instance(p).applyTo(pg);
		} else if(curFilter == HueFilter.instance(p)) {
			ColorizeFromTexture.instance(p).applyTo(pg);
			
			HueFilter.instance(p).setHue(p.mousePercentX() * 360f);
			HueFilter.instance(p).applyTo(pg);
		} else if(curFilter == InvertFilter.instance(p)) {
			InvertFilter.instance(p).applyTo(pg);
		} else if(curFilter == KaleidoFilter.instance(p)) {
			KaleidoFilter.instance(p).setAngle(p.mousePercentX() * P.TWO_PI);
			KaleidoFilter.instance(p).setSides(p.mousePercentY() * 16f);
			KaleidoFilter.instance(p).applyTo(pg);
		} else if(curFilter == LeaveBlackFilter.instance(p)) {
			LeaveBlackFilter.instance(p).setMix(p.mousePercentX());
			LeaveBlackFilter.instance(p).applyTo(pg);
		} else if(curFilter == LeaveWhiteFilter.instance(p)) {
			LeaveWhiteFilter.instance(p).setMix(p.mousePercentX());
			LeaveWhiteFilter.instance(p).applyTo(pg);
		} else if(curFilter == LiquidWarpFilter.instance(p)) {
//			setAmplitude(0.02f);
//			setFrequency(6.0f);
			LiquidWarpFilter.instance(p).setTime(p.frameCount * 0.01f);
			LiquidWarpFilter.instance(p).setAmplitude(p.mousePercentX() * 0.1f);
			LiquidWarpFilter.instance(p).setFrequency(p.mousePercentY() * 20f);
			LiquidWarpFilter.instance(p).applyTo(pg);
		} else if(curFilter == MaskThreeTextureFilter.instance(p)) {
			ThresholdFilter.instance(p).setCutoff(0.5f);
			ThresholdFilter.instance(p).applyTo(pg);

			MaskThreeTextureFilter.instance(p).setMask(pg);
			MaskThreeTextureFilter.instance(p).setTexture1(DemoAssets.justin());
			MaskThreeTextureFilter.instance(p).setTexture2(DemoAssets.textureNebula());
			MaskThreeTextureFilter.instance(p).applyTo(pg);
		} else if(curFilter == MirrorFilter.instance(p)) {
			MirrorFilter.instance(p).applyTo(pg);
		} else 
			if(curFilter == PixelateFilter.instance(p)) {
			PixelateFilter.instance(p).setDivider(p.mousePercentX() * 100f, p.width, p.height);
			PixelateFilter.instance(p).applyTo(pg);
		} else if(curFilter == RadialBlurFilter.instance(p)) {
			RadialBlurFilter.instance(p).applyTo(pg);
		} else if(curFilter == RadialRipplesFilter.instance(p)) {
			RadialRipplesFilter.instance(p).setTime(p.frameCount * 0.01f);
			RadialRipplesFilter.instance(p).setAmplitude(p.mousePercentX() * 4f);
			RadialRipplesFilter.instance(p).applyTo(pg);
		} else if(curFilter == RotateFilter.instance(p)) {
			RotateFilter.instance(p).setRotation(p.mousePercentX() * P.TWO_PI);
			RotateFilter.instance(p).applyTo(pg);
		} else if(curFilter == SaturateHSVFilter.instance(p)) {
			ColorizeFromTexture.instance(p).applyTo(pg);
			
			SaturateHSVFilter.instance(p).setSaturation(p.mousePercentX() * 10f);
			SaturateHSVFilter.instance(p).applyTo(pg);
		} else if(curFilter == SaturationFilter.instance(p)) {
			ColorizeFromTexture.instance(p).applyTo(pg);
			
			SaturationFilter.instance(p).setSaturation(p.mousePercentX() * 10f);
			SaturationFilter.instance(p).applyTo(pg);
		} else if(curFilter == SharpenFilter.instance(p)) {
			SharpenFilter.instance(p).setSharpness(p.mousePercentX() * 10f);
			SharpenFilter.instance(p).applyTo(pg);
		} else if(curFilter == SphereDistortionFilter.instance(p)) {
			SphereDistortionFilter.instance(p).setAmplitude(P.map(p.mouseX, 0, p.width, -20f, 20f));
			SphereDistortionFilter.instance(p).applyTo(pg);
		} else if(curFilter == ThresholdFilter.instance(p)) {
			ThresholdFilter.instance(p).setCutoff(p.mousePercentX());
			ThresholdFilter.instance(p).applyTo(pg);
		} else if(curFilter == VignetteAltFilter.instance(p)) {
			VignetteAltFilter.instance(p).setDarkness(-5f + 10f * p.mousePercentX());
			VignetteAltFilter.instance(p).setSpread(p.mousePercentY() * 5f);
			VignetteAltFilter.instance(p).applyTo(pg);
		} else if(curFilter == VignetteFilter.instance(p)) {
			VignetteFilter.instance(p).setDarkness(-5f + 10f * p.mousePercentX());
			VignetteFilter.instance(p).setSpread(p.mousePercentY() * 5f);
			VignetteFilter.instance(p).applyTo(pg);
		} else if(curFilter == WarperFilter.instance(p)) {
			WarperFilter.instance(p).applyTo(pg);
		} else if(curFilter == WobbleFilter.instance(p)) {
//			setSpeed(1f);
//			setStrength(0.001f);
//			setSize(100f);
			WobbleFilter.instance(p).setTime(p.frameCount * 0.01f);
			WobbleFilter.instance(p).setSpeed(2f); // p.mousePercentX() * 3f);
			WobbleFilter.instance(p).setStrength(p.mousePercentX());
			WobbleFilter.instance(p).setSize(p.mousePercentY() * 5f);
			WobbleFilter.instance(p).applyTo(pg);
		} 
		
		// draw custom filter for testing
		if(customShader != null && triggerToggle.on() == false) pg.filter(customShader);
		
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

}
