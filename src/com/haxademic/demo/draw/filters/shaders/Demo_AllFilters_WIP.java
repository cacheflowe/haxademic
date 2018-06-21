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
import com.haxademic.core.draw.filters.shaders.ColorizeFromTexture;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilter;
import com.haxademic.core.draw.filters.shaders.CubicLensDistortionFilterOscillate;
import com.haxademic.core.draw.filters.shaders.EdgeColorDarkenFilter;
import com.haxademic.core.draw.filters.shaders.GodRays;
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
	
	protected int filterIndex = 0;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 800 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 800 );
	}

	public void setupFirstFrame() {
		texture = new TextureShader(TextureShader.bw_clouds);
		
//		customShader = p.loadShader(FileUtil.getFile("haxademic/shaders/filters/godrays.glsl"));
	}
	
	protected float oscillate() {
		return P.sin(p.frameCount * 0.01f);
	}
	
	public void drawApp() {
		// cycle
		int numEffects = 12;
		if(triggerPrev.triggered()) filterIndex = (filterIndex > 0) ? filterIndex - 1 : numEffects - 1;
		if(triggerNext.triggered()) filterIndex = (filterIndex < numEffects - 1) ? filterIndex + 1 : 0;

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
		
		// apply some filters
		DrawUtil.setTextureRepeat(p, true);
		if(filterIndex == 0) {
	 		CubicLensDistortionFilter.instance(p).setAmplitude(P.map(p.mouseX, 0, p.width, -20f, 20f));
			CubicLensDistortionFilter.instance(p).setSeparation(P.map(p.mouseY, 0, p.height, 0, 3f));
			CubicLensDistortionFilter.instance(p).applyTo(p);
			filterName = "CubicLensDistortionFilter";
		} else if(filterIndex == 1) {
			// old distortion
			CubicLensDistortionFilterOscillate.instance(p).setTime(p.frameCount * 0.01f);
			CubicLensDistortionFilterOscillate.instance(p).applyTo(p);
			filterName = "CubicLensDistortionFilterOscillate";
		} else if(filterIndex == 2) {
			EdgeColorDarkenFilter.instance(p).setSpreadX(p.mousePercentX());
			EdgeColorDarkenFilter.instance(p).setSpreadY(p.mousePercentY());
			EdgeColorDarkenFilter.instance(p).applyTo(p);
			filterName = "EdgeColorDarkenFilter";
		} else if(filterIndex == 3) {
			GodRays.instance(p).setDecay(p.mousePercentX());
			GodRays.instance(p).setWeight(p.mousePercentY());
			GodRays.instance(p).setRotation(oscillate());
			GodRays.instance(p).setAmp(0.5f + 0.5f * oscillate());
			GodRays.instance(p).applyTo(p);
			filterName = "GodRays";
		} else if(filterIndex == 4) {
			BadTVGlitchFilter.instance(p).setTime(p.frameCount * 0.01f);
			BadTVGlitchFilter.instance(p).applyTo(p);
			filterName = "BadTVGlitchFilter";
		} else if(filterIndex == 5) {
			BadTVLinesFilter.instance(p).setTime(p.frameCount * 0.01f);
			BadTVLinesFilter.instance(p).setGrayscale(0);
			BadTVLinesFilter.instance(p).setIntensityN(p.mousePercentX());
			BadTVLinesFilter.instance(p).setIntensityS(p.mousePercentY());
			BadTVLinesFilter.instance(p).setCountS(4096.0f);
			BadTVLinesFilter.instance(p).applyTo(p);
			filterName = "BadTVLinesFilter";
		} else if(filterIndex == 6) {
			BlurBasicFilter.instance(p).applyTo(p);
			filterName = "BlurBasicFilter";
		} else if(filterIndex == 7) {
			BlurHFilter.instance(p).setBlurByPercent(p.mousePercentX() * 2f, p.width);
			BlurHFilter.instance(p).applyTo(p);
			BlurVFilter.instance(p).setBlurByPercent(p.mousePercentY() * 2f, p.height);
			BlurVFilter.instance(p).applyTo(p);
			filterName = "BlurHFilter + BlurVFilter";
		} else if(filterIndex == 8) {
			BlurProcessingFilter.instance(p).setBlurSize(P.round(p.mousePercentY() * 10f));
			BlurProcessingFilter.instance(p).setSigma(p.mousePercentX() * 10f);
			BlurProcessingFilter.instance(p).applyTo(p);
			filterName = "BlurProcessingFilter";
		} else if(filterIndex == 9) {
			BrightnessFilter.instance(p).setBrightness(p.mousePercentY() * 10f);
			BrightnessFilter.instance(p).applyTo(p);
			filterName = "BrightnessFilter";
		} else if(filterIndex == 10) {
			ColorCorrectionFilter.instance(p).setContrast(p.mousePercentX() * 10f);
			ColorCorrectionFilter.instance(p).setGamma(p.mousePercentY() * 10f);
			ColorCorrectionFilter.instance(p).applyTo(p);
			filterName = "ColorCorrectionFilter";
		} else if(filterIndex == 11) {
			ColorizeFromTexture.instance(p).setTexture(ImageGradient.PASTELS());
			ColorizeFromTexture.instance(p).applyTo(p);
//			imageGradient = new ImageGradient(ImageGradient.PASTELS());
//			imageGradient.addTexturesFromPath(ImageGradient.COOLORS_PATH);
//			imageGradient.randomGradientTexture();
			filterName = "ColorizeFromTexture";
		}
		p.text(filterName, 20, p.height - 30);
		
		// custom filter
		// customShader.set("rotation", P.sin(p.frameCount * 0.01f));
		if(customShader != null && triggerToggle.on() == false) p.filter(customShader);
	}

}
