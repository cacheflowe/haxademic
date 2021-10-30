package com.haxademic.core.draw.filters.pshader.compound;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ContrastFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;
import com.haxademic.core.draw.filters.pshader.ToneMappingFilter;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;

public class ColorAdjustmentFilter {

	// tonemapping UI
	public static String MODE = "ColorAdjustmentFilter.MODE";
	public static String GAMMA = "ColorAdjustmentFilter.GAMMA";
	public static String CROSSFADE = "ColorAdjustmentFilter.CROSSFADE";
	// extra adjustments UI
	public static String SATURATION = "ColorAdjustmentFilter.SATURATION";
	public static String CONTRAST = "ColorAdjustmentFilter.CONTRAST";
	public static String BRIGHTNESS = "ColorAdjustmentFilter.BRIGHTNESS";
	public static String SHARPEN = "ColorAdjustmentFilter.SHARPEN";
	
	public static boolean hasUI = false;
	
	public ColorAdjustmentFilter() {}
	
	public static void initUI() {
		if(hasUI) {
			P.error("[ERROR] ColorAdjustmentFilter UI already initialized!");
			return;
		}
		hasUI = true;
		
		// add tonemapping UI
		UI.addTitle("Tonemapping");
		UI.addSlider(MODE, 1, 0, 9, 1, true);
		UI.addSlider(GAMMA, 2.2f, 0, 10, 0.01f, true);
		UI.addSlider(CROSSFADE, 1, 0, 1, 0.01f, true);
		
		// extra controls
		UI.addTitle("Postprocessing");
		UI.addSlider(CONTRAST, 1, 0, 3, 0.01f, true);
		UI.addSlider(SATURATION, 1, 0, 3, 0.01f, true);
		UI.addSlider(BRIGHTNESS, 1, 0, 3, 0.01f, true);
		UI.addSlider(SHARPEN, 0, 0, 3, 0.01f, true);
	}
	
	public static void applyFromUI(PGraphics pg) {
		applyTo(
			pg,
			UI.valueInt(MODE),
			UI.value(GAMMA),
			UI.value(CROSSFADE),
			UI.value(BRIGHTNESS),
			UI.value(CONTRAST),
			UI.value(SHARPEN),
			UI.value(SATURATION)
		);
	}
	
	public static void applyTo(PGraphics pg, int mode, float gamma, float crossfade, float brightness, float contrast, float sharpen, float saturation) {
		// tonemapping (mode 0 doesn't do anything)
		if(mode > 0) {
			ToneMappingFilter.instance(P.p).setMode(mode);
			ToneMappingFilter.instance(P.p).setGamma(gamma);
			ToneMappingFilter.instance(P.p).setCrossfade(crossfade);
			ToneMappingFilter.instance(P.p).applyTo(pg);
		}
			
		// extra controls as needed 
		if(brightness != 1f) {
			BrightnessFilter.instance(P.p).setBrightness(brightness);
			BrightnessFilter.instance(P.p).applyTo(pg);
		}
		if(contrast != 1f) {
			ContrastFilter.instance(P.p).setContrast(contrast);
			ContrastFilter.instance(P.p).applyTo(pg);
		}
		if(sharpen != 0) {
			SharpenFilter.instance(P.p).setSharpness(sharpen);
			SharpenFilter.instance(P.p).applyTo(pg);
		}
		if(saturation != 1f) {
			SaturationFilter.instance(P.p).setSaturation(saturation);
			SaturationFilter.instance(P.p).applyTo(pg);
		}
	}
	
}
