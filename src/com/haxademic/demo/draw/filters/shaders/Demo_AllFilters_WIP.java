package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.draw.color.ImageGradient;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.filters.shaders.BadTVGlitchFilter;
import com.haxademic.core.draw.filters.shaders.BadTVLinesFilter;
import com.haxademic.core.draw.filters.shaders.BlurBasicFilter;
import com.haxademic.core.draw.filters.shaders.BlurHFilter;
import com.haxademic.core.draw.filters.shaders.BlurProcessingFilter;
import com.haxademic.core.draw.filters.shaders.BlurVFilter;
import com.haxademic.core.draw.filters.shaders.BrightnessFilter;
import com.haxademic.core.draw.filters.shaders.ColorCorrectionFilter;
import com.haxademic.core.draw.filters.shaders.ColorizeFilter;
import com.haxademic.core.draw.filters.shaders.ColorizeFromTexture;
import com.haxademic.core.draw.filters.shaders.ColorizeTwoColorsFilter;
import com.haxademic.core.draw.filters.shaders.ContrastFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilterOscillate;
import com.haxademic.core.draw.filters.shaders.DeformBloomFilter;
import com.haxademic.core.draw.filters.shaders.DilateFilter;
import com.haxademic.core.draw.filters.shaders.EdgeColorDarkenFilter;
import com.haxademic.core.draw.filters.shaders.EdgeColorFadeFilter;
import com.haxademic.core.draw.filters.shaders.EdgesFilter;
import com.haxademic.core.draw.filters.shaders.EmbossFilter;
import com.haxademic.core.draw.filters.shaders.ErosionFilter;
import com.haxademic.core.draw.filters.shaders.FXAAFilter;
import com.haxademic.core.draw.filters.shaders.GodRays;
import com.haxademic.core.draw.filters.shaders.GradientCoverWipe;
import com.haxademic.core.draw.filters.shaders.HalftoneFilter;
import com.haxademic.core.draw.filters.shaders.HalftoneLinesFilter;
import com.haxademic.core.draw.filters.shaders.InvertFilter;
import com.haxademic.core.draw.filters.shaders.PixelateFilter;
import com.haxademic.core.draw.filters.shaders.RadialRipplesFilter;
import com.haxademic.core.draw.filters.shaders.RotateFilter;
import com.haxademic.core.draw.filters.shaders.SharpenFilter;
import com.haxademic.core.draw.filters.shaders.SphereDistortionFilter;
import com.haxademic.core.draw.filters.shaders.ThresholdFilter;
import com.haxademic.core.draw.filters.shaders.VignetteAltFilter;
import com.haxademic.core.draw.filters.shaders.VignetteFilter;
import com.haxademic.core.draw.filters.shaders.WarperFilter;
import com.haxademic.core.draw.filters.shaders.WobbleFilter;
import com.haxademic.core.draw.filters.shaders.shared.BaseFilter;
import com.haxademic.core.draw.shaders.textures.TextureShader;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.hardware.shared.InputTrigger;

import processing.opengl.PShader;

public class Demo_AllFilters_WIP
extends PAppletHax { public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

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
			CubicLensDistortionFilter.instance(p),
			CubicLensDistortionFilterOscillate.instance(p),
			EdgeColorDarkenFilter.instance(p),
			GodRays.instance(p),
			BadTVGlitchFilter.instance(p),
			BadTVLinesFilter.instance(p),
			BlurBasicFilter.instance(p),
			BlurHFilter.instance(p),
			BlurProcessingFilter.instance(p),
			BrightnessFilter.instance(p),
			ColorCorrectionFilter.instance(p),
			ColorizeFilter.instance(p),
			ColorizeFromTexture.instance(p),
			ColorizeTwoColorsFilter.instance(p),
			ContrastFilter.instance(p),
			DeformBloomFilter.instance(p),
			DilateFilter.instance(p),
			EdgesFilter.instance(p),
			EmbossFilter.instance(p),
			ErosionFilter.instance(p),
			FXAAFilter.instance(p),
			HalftoneFilter.instance(p),
			HalftoneLinesFilter.instance(p),
			InvertFilter.instance(p),
			PixelateFilter.instance(p),
			RadialRipplesFilter.instance(p),
			RotateFilter.instance(p),
			SharpenFilter.instance(p),
			SphereDistortionFilter.instance(p),
			ThresholdFilter.instance(p),
			VignetteAltFilter.instance(p),
			VignetteFilter.instance(p),
			WarperFilter.instance(p),
			WobbleFilter.instance(p),
			GradientCoverWipe.instance(p),
		};

		texture = new TextureShader(TextureShader.bw_clouds);
		
//		customShader = p.loadShader(FileUtil.getFile("haxademic/shaders/filters/godrays.glsl"));
	}
	
	protected float oscillate() {
		return P.sin(p.frameCount * 0.01f);
	}
	
	public void drawApp() {
		// cycle through effects
		int numEffects = filters.length;
		if(triggerPrev.triggered()) filterIndex = (filterIndex > 0) ? filterIndex - 1 : numEffects - 1;
		if(triggerNext.triggered()) filterIndex = (filterIndex < numEffects - 1) ? filterIndex + 1 : 0;

		// debug log mouse position
		p.debugView.setValue("p.mousePercentX()", p.mousePercentX());
		p.debugView.setValue("p.mousePercentY()", p.mousePercentY());
		
		// update cur shader & draw to screen
		texture.updateTime();
		p.filter(texture.shader());
		
		// draw some text to make sure we know orientation
		p.fill(127 + 127f * P.sin(p.frameCount * 0.01f));
		p.textFont(DemoAssets.fontBitlow(100));
		p.textAlign(P.CENTER, P.CENTER);
		p.text("FILTER", 0, 0, p.width, p.height);
		
		// set up context for more text
		p.fill(255);
		p.textAlign(P.LEFT, P.CENTER);
		p.textFont(DemoAssets.fontRaleway(20));
		String filterName = "";
		
		// apply active filter
		DrawUtil.setTextureRepeat(p, true);
		
		BaseFilter curFilter = filters[filterIndex];
		if(curFilter == CubicLensDistortionFilter.instance(p)) {
	 		CubicLensDistortionFilter.instance(p).setAmplitude(P.map(p.mouseX, 0, p.width, -20f, 20f));
			CubicLensDistortionFilter.instance(p).setSeparation(P.map(p.mouseY, 0, p.height, 0, 3f));
			CubicLensDistortionFilter.instance(p).applyTo(p);
		} else if(curFilter == CubicLensDistortionFilterOscillate.instance(p)) {
			// old distortion
			CubicLensDistortionFilterOscillate.instance(p).setTime(p.frameCount * 0.01f);
			CubicLensDistortionFilterOscillate.instance(p).applyTo(p);
		} else if(curFilter == EdgeColorDarkenFilter.instance(p)) {
			EdgeColorDarkenFilter.instance(p).setSpreadX(p.mousePercentX());
			EdgeColorDarkenFilter.instance(p).setSpreadY(p.mousePercentY());
			EdgeColorDarkenFilter.instance(p).applyTo(p);
		} else if(curFilter == GodRays.instance(p)) {
			GodRays.instance(p).setDecay(p.mousePercentX());
			GodRays.instance(p).setWeight(p.mousePercentY());
			GodRays.instance(p).setRotation(oscillate());
			GodRays.instance(p).setAmp(0.5f + 0.5f * oscillate());
			GodRays.instance(p).applyTo(p);
		} else if(curFilter == BadTVGlitchFilter.instance(p)) {
			BadTVGlitchFilter.instance(p).setTime(p.frameCount * 0.01f);
			BadTVGlitchFilter.instance(p).applyTo(p);
		} else if(curFilter == BadTVLinesFilter.instance(p)) {
			BadTVLinesFilter.instance(p).setTime(p.frameCount * 0.01f);
			BadTVLinesFilter.instance(p).setGrayscale(0);
			BadTVLinesFilter.instance(p).setIntensityN(p.mousePercentX());
			BadTVLinesFilter.instance(p).setIntensityS(p.mousePercentY());
			BadTVLinesFilter.instance(p).setCountS(4096.0f);
			BadTVLinesFilter.instance(p).applyTo(p);
		} else if(curFilter == BlurBasicFilter.instance(p)) {
			BlurBasicFilter.instance(p).applyTo(p);
		} else if(curFilter == BlurHFilter.instance(p)) {
			BlurHFilter.instance(p).setBlurByPercent(p.mousePercentX() * 2f, p.width);
			BlurHFilter.instance(p).applyTo(p);
			BlurVFilter.instance(p).setBlurByPercent(p.mousePercentY() * 2f, p.height);
			BlurVFilter.instance(p).applyTo(p);
		} else if(curFilter == BlurProcessingFilter.instance(p)) {
			BlurProcessingFilter.instance(p).setBlurSize(P.round(p.mousePercentY() * 10f));
			BlurProcessingFilter.instance(p).setSigma(p.mousePercentX() * 10f);
			BlurProcessingFilter.instance(p).applyTo(p);
		} else if(curFilter == BrightnessFilter.instance(p)) {
			BrightnessFilter.instance(p).setBrightness(p.mousePercentY() * 10f);
			BrightnessFilter.instance(p).applyTo(p);
		} else if(curFilter == ColorCorrectionFilter.instance(p)) {
			ColorCorrectionFilter.instance(p).setContrast(p.mousePercentX() * 10f);
			ColorCorrectionFilter.instance(p).setGamma(p.mousePercentY() * 10f);
			ColorCorrectionFilter.instance(p).applyTo(p);
		} else if(curFilter == ColorizeFilter.instance(p)) {
			ColorizeFilter.instance(p).setTargetR(p.mousePercentX());
			ColorizeFilter.instance(p).setTargetG(p.mousePercentY());
			ColorizeFilter.instance(p).setTargetB(p.mousePercentX());
			ColorizeFilter.instance(p).applyTo(p);
		} else if(curFilter == ColorizeFromTexture.instance(p)) {
			ColorizeFromTexture.instance(p).setTexture(ImageGradient.PASTELS());
			ColorizeFromTexture.instance(p).setLumaMult(p.mousePercentX() > 0.5f);
			ColorizeFromTexture.instance(p).setCrossfade(p.mousePercentY());
			ColorizeFromTexture.instance(p).applyTo(p);
		} else if(curFilter == ColorizeTwoColorsFilter.instance(p)) {
			ColorizeTwoColorsFilter.instance(p).setColor1(1f, 0f, 1f);
			ColorizeTwoColorsFilter.instance(p).setColor2(0f, 1f, 1f);
			ColorizeTwoColorsFilter.instance(p).applyTo(p);
		} else if(curFilter == ContrastFilter.instance(p)) {
			ContrastFilter.instance(p).setContrast(p.mousePercentX() * 3);
			ContrastFilter.instance(p).applyTo(p);
//		} else if(curFilter == DeformBloomFilter.instance(p)) {
//			DeformBloomFilter.instance(p).applyTo(p);
		} else if(curFilter == DilateFilter.instance(p)) {
			DilateFilter.instance(p).applyTo(p);
		} else if(curFilter == EdgeColorFadeFilter.instance(p)) {
			EdgeColorFadeFilter.instance(p).setEdgeColor(1f, 0f, 0f);
			EdgeColorFadeFilter.instance(p).setSpreadX(p.mousePercentX());
			EdgeColorFadeFilter.instance(p).setSpreadY(p.mousePercentY());
			EdgeColorFadeFilter.instance(p).applyTo(p);
		} else if(curFilter == EdgesFilter.instance(p)) {
			EdgesFilter.instance(p).applyTo(p);
		} else if(curFilter == EmbossFilter.instance(p)) {
			EmbossFilter.instance(p).applyTo(p);
		} else if(curFilter == ErosionFilter.instance(p)) {
			ErosionFilter.instance(p).applyTo(p);
		} else if(curFilter == FXAAFilter.instance(p)) {
			FXAAFilter.instance(p).applyTo(p);
		} else if(curFilter == HalftoneFilter.instance(p)) {
			HalftoneFilter.instance(p).setAngle(p.mousePercentX() * P.TWO_PI);
//			HalftoneFilter.instance(p).setScale(p.mousePercentY() * 3f);
//			HalftoneFilter.instance(p).setSizeT(p.mousePercentY() * 1024f, p.mousePercentY() * 1024f);
			HalftoneFilter.instance(p).setCenter(p.mousePercentX(), p.mousePercentY());
			HalftoneFilter.instance(p).applyTo(p);
		} else if(curFilter == HalftoneLinesFilter.instance(p)) {
//			setSampleDistX(200f);   // divisions for kernel sampling (width)
//			setSampleDistY(80f);	// divisions for kernel sampling (height)
			HalftoneLinesFilter.instance(p).setRows(p.mousePercentY() * 150f);
			HalftoneLinesFilter.instance(p).setRotation(p.mousePercentX() * P.TWO_PI);
//			setRotation(0f);
//			setAntiAlias(0.1f);
//			setMode(3);
			HalftoneLinesFilter.instance(p).applyTo(p);
		} else if(curFilter == InvertFilter.instance(p)) {
			InvertFilter.instance(p).applyTo(p);
		} else if(curFilter == PixelateFilter.instance(p)) {
			PixelateFilter.instance(p).setDivider(P.round(p.mousePercentX() * 100f), p.width, p.height);
			PixelateFilter.instance(p).applyTo(p);
		} else if(curFilter == RadialRipplesFilter.instance(p)) {
			RadialRipplesFilter.instance(p).setTime(p.frameCount * 0.01f);
			RadialRipplesFilter.instance(p).setAmplitude(p.mousePercentX() * 4f);
			RadialRipplesFilter.instance(p).applyTo(p);
		} else if(curFilter == RotateFilter.instance(p)) {
			RotateFilter.instance(p).setRotation(p.mousePercentX() * P.TWO_PI);
			RotateFilter.instance(p).applyTo(p);
		} else if(curFilter == SharpenFilter.instance(p)) {
			SharpenFilter.instance(p).setSharpness(p.mousePercentX() * 10f);
			SharpenFilter.instance(p).applyTo(p);
		} else if(curFilter == SphereDistortionFilter.instance(p)) {
			SphereDistortionFilter.instance(p).setAmplitude(P.map(p.mouseX, 0, p.width, -20f, 20f));
			SphereDistortionFilter.instance(p).applyTo(p);
		} else if(curFilter == ThresholdFilter.instance(p)) {
			ThresholdFilter.instance(p).setCutoff(p.mousePercentX());
			ThresholdFilter.instance(p).applyTo(p);
		} else if(curFilter == VignetteAltFilter.instance(p)) {
			VignetteAltFilter.instance(p).setDarkness(-5f + 10f * p.mousePercentX());
			VignetteAltFilter.instance(p).setSpread(p.mousePercentY() * 5f);
			VignetteAltFilter.instance(p).applyTo(p);
		} else if(curFilter == VignetteFilter.instance(p)) {
			VignetteFilter.instance(p).setDarkness(-5f + 10f * p.mousePercentX());
			VignetteFilter.instance(p).setSpread(p.mousePercentY() * 5f);
			VignetteFilter.instance(p).applyTo(p);
		} else if(curFilter == WarperFilter.instance(p)) {
			WarperFilter.instance(p).applyTo(p);
		} else if(curFilter == WobbleFilter.instance(p)) {
//			setSpeed(1f);
//			setStrength(0.001f);
//			setSize(100f);
			WobbleFilter.instance(p).setTime(p.frameCount * 0.01f);
			WobbleFilter.instance(p).setSpeed(2f); // p.mousePercentX() * 3f);
			WobbleFilter.instance(p).setStrength(p.mousePercentX());
			WobbleFilter.instance(p).setSize(p.mousePercentY() * 5f);
			WobbleFilter.instance(p).applyTo(p);
		} else if(curFilter == GradientCoverWipe.instance(p)) {
			GradientCoverWipe.instance(p).setColorTop(1f, 0f, 1f, 1f);
			GradientCoverWipe.instance(p).setColorBot(0f, 1f, 1f, 1f);
			GradientCoverWipe.instance(p).setProgress(p.mousePercentX());
			GradientCoverWipe.instance(p).applyTo(p);
		}
		filterName = curFilter.getClass().getSimpleName();
		p.text(filterName, 20, p.height - 30);
		
		// custom filter
		if(customShader != null && triggerToggle.on() == false) p.filter(customShader);
	}

}
