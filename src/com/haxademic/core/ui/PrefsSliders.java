package com.haxademic.core.ui;

import java.util.HashMap;

import com.haxademic.core.app.P;

import processing.core.PFont;

public class PrefsSliders {

	protected HashMap<String, PrefSlider> prefSliders;
	
	protected int sliderX = 10;
	protected int sliderY = 10;
	protected int sliderW = 300;
	protected int sliderH = 20;
	protected int sliderSpacing = 24;
	
	protected PFont debugFont;
	protected boolean active = false;

	public PrefsSliders() {
		prefSliders = new HashMap<String, PrefSlider>();
		debugFont = P.p.createFont("Arial", 12);
	}
	
	public void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep) {
		addSlider(key, value, valueLow, valueHigh, dragStep, true);
	}
	
	public void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves) {
		prefSliders.put(key, new PrefSlider(key, value, valueLow, valueHigh, dragStep, sliderX, sliderY, sliderW, sliderH, saves));
		sliderY += sliderSpacing;
	}
	
	public void removeSlider(String key) {
		prefSliders.remove(key);
	}
	
	public float value(String key) {
		return prefSliders.get(key).value();
	}
	
	public int valueInt(String key) {
		return P.round(prefSliders.get(key).value());
	}
	
	public void update() {
		if(!active) return;
		P.p.textFont(debugFont);
		for (PrefSlider prefSlider : prefSliders.values()) {
			prefSlider.update(P.p.g);
		}
	}

	public void active(boolean val) {
		active = val;
	}

	public boolean active() {
		return active;
	}
	
}