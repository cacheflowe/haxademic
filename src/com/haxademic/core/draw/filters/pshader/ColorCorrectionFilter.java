package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.ui.UI;

public class ColorCorrectionFilter
extends BaseFragmentShader {

	public static ColorCorrectionFilter instance;

	public static String BRIGHTNESS = "BRIGHTNESS";
	public static String CONTRAST = "CONTRAST";
	public static String GAMMA = "GAMMA";
	
	public ColorCorrectionFilter() {
		super("haxademic/shaders/filters/color-correction.glsl");
		setBrightness(0f);
		setContrast(1f);
		setGamma(1f);
	}
	
	public static ColorCorrectionFilter instance() {
		if(instance != null) return instance;
		instance = new ColorCorrectionFilter();
		return instance;
	}
	
	public void setBrightness(float brightness) {
		shader.set("brightness", brightness);
	}
	
	public void setContrast(float contrast) {
		shader.set("contrast", contrast);
	}
	
	public void setGamma(float gamma) {
		shader.set("gamma", gamma);
	}
	
	public void buildUI(String id, boolean saves) {
		UI.addTitle("COLOR_CORRECTION | " + id);
		UI.addSlider(id + BRIGHTNESS, 0, -1, 1, 0.01f, saves);
		UI.addSlider(id + CONTRAST, 1, 0, 4, 0.01f, saves);
		UI.addSlider(id + GAMMA, 1, 0, 5, 0.01f, saves);
	}

	public void setPropsFromUI(String id) {
		setBrightness(UI.value(id + BRIGHTNESS));
		setContrast(UI.value(id + CONTRAST));
		setGamma(UI.value(id + GAMMA));
	}

}
