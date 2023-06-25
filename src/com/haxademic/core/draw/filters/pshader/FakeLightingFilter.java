package com.haxademic.core.draw.filters.pshader;

import com.haxademic.core.draw.filters.pshader.shared.BaseFragmentShader;
import com.haxademic.core.ui.UI;

import processing.core.PImage;

public class FakeLightingFilter
extends BaseFragmentShader {

	public static FakeLightingFilter instance;

	public static String AMBIENT = "AMBIENT";
	public static String GRAD_AMP = "GRAD_AMP";
	public static String GRAD_BLUR = "GRAD_BLUR";
	public static String SPEC_AMP = "SPEC_AMP";
	public static String DIFF_DARK = "DIFF_DARK";
	public static String FILTER_ACTIVE = "FILTER_ACTIVE";

	public FakeLightingFilter() {
		super("haxademic/shaders/filters/fake-lighting.glsl");
		setAmbient(4.0f);
		setGradAmp(1.0f);
		setGradBlur(1.0f);
		setSpecAmp(1.5f);
		setDiffDark(0.5f);
		setMap(null);
	}
	
	public static FakeLightingFilter instance() {
		if(instance != null) return instance;
		instance = new FakeLightingFilter();
		return instance;
	}
	
	public void setAmbient(float ambient) {
		shader.set("ambient", ambient);
	}

	public void setGradAmp(float gradAmp) {
		shader.set("gradAmp", gradAmp);
	}
	
	public void setGradBlur(float gradBlur) {
		shader.set("gradBlur", gradBlur);
	}
	
	public void setSpecAmp(float specAmp) {
		shader.set("specAmp", specAmp);
	}
	
	public void setDiffDark(float diffDark) {
		shader.set("diffDark", diffDark);
	}

	public void setMap(PImage map) {
		shader.set("map", map);
	}

	// UI

	public void buildUI() {
		buildUI("FAKE_LIGHTING", false);
	}

	public void buildUI(String id, boolean saves) {
		UI.addTitle("FAKE_LIGHTING | " + id);
		UI.addSlider(id + AMBIENT, 2f, 0.3f, 6f, 0.01f, saves);
		UI.addSlider(id + GRAD_AMP, 0.66f, 0f, 6f, 0.005f, saves);
		UI.addSlider(id + GRAD_BLUR, 1f, 0.1f, 6f, 0.01f, saves);
		UI.addSlider(id + SPEC_AMP, 2.25f, 0.1f, 6f, 0.01f, saves);
		UI.addSlider(id + DIFF_DARK, 0.85f, 0.1f, 2f, 0.01f, saves);
	}

	public void setPropsFromUI(String id) {
		setAmbient(UI.value(id + AMBIENT));
		setGradAmp(UI.value(id + GRAD_AMP));
		setGradBlur(UI.value(id + GRAD_BLUR));
		setSpecAmp(UI.value(id + SPEC_AMP));
		setDiffDark(UI.value(id + DIFF_DARK));
	}

}