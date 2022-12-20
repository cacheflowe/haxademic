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
	public static String MODE = "ADJUST.MODE.";
	public static String GAMMA = "ADJUST.GAMMA.";
	public static String CROSSFADE = "ADJUST.CROSSFADE.";
	// extra adjustments UI
	public static String SATURATION = "ADJUST.SATURATION.";
	public static String CONTRAST = "ADJUST.CONTRAST.";
	public static String BRIGHTNESS = "ADJUST.BRIGHTNESS.";
	public static String SHARPEN = "ADJUST.SHARPEN.";

	public static final String DEFAULT_ID = "_";
	
	public ColorAdjustmentFilter() {}
	
	public static void buildUI() {
	    buildUI(DEFAULT_ID, true);
	}
	
	public static void buildUI(String uiID, boolean saveValues) {
		if(UI.has(MODE + uiID)) {
			P.error("[ColorAdjustmentFilter ERROR] UI already initialized for this ID!");
			return;
		}
		
		// add tonemapping UI
		UI.addTitle("Tonemapping | " + uiID);
		UI.addSlider(MODE + uiID, 1, 0, 9, 1, saveValues);
		UI.addSlider(GAMMA + uiID, 2.2f, 0, 10, 0.01f, saveValues);
		UI.addSlider(CROSSFADE + uiID, 1, 0, 1, 0.01f, saveValues);
		
		// extra controls
		UI.addTitle("Postprocessing | " + uiID);
		UI.addSlider(CONTRAST + uiID, 1, 0, 3, 0.01f, saveValues);
		UI.addSlider(SATURATION + uiID, 1, 0, 3, 0.01f, saveValues);
		UI.addSlider(BRIGHTNESS + uiID, 1, 0, 3, 0.01f, saveValues);
		UI.addSlider(SHARPEN + uiID, 0, 0, 3, 0.01f, saveValues);
	}
	
	public static void applyFromUI(PGraphics pg) {
	    applyFromUI(pg, DEFAULT_ID);
	}
	
	public static void applyFromUI(PGraphics pg, String uiID) {
	    if(!UI.has(MODE + uiID)) {
	        P.error("[ColorAdjustmentFilter ERROR] No UI for this ID!");
	        return;
	    }

		applyTo(
			pg,
			UI.valueInt(MODE + uiID),
			UI.value(GAMMA + uiID),
			UI.value(CROSSFADE + uiID),
			UI.value(BRIGHTNESS + uiID),
			UI.value(CONTRAST + uiID),
			UI.value(SHARPEN + uiID),
			UI.value(SATURATION + uiID)
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
