package com.haxademic.core.ui;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.file.DemoAssets;

import processing.core.PFont;
import processing.data.JSONObject;

public class PrefsSliders {

	protected HashMap<String, PrefSlider> prefSliders;
	
	protected int sliderX = 10;
	protected int sliderY = 10;
	protected int sliderW = 300;
	protected int sliderH = 14;
	protected int sliderSpacing = 18;
	
	protected PFont debugFont;
	protected boolean active = false;

	public PrefsSliders() {
		prefSliders = new HashMap<String, PrefSlider>();
		debugFont = DemoAssets.fontOpenSans(10); // P.p.createFont("Arial", 10);
	}
	
	public void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep) {
		addSlider(key, value, valueLow, valueHigh, dragStep, true);
	}
	
	public void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves) {
		prefSliders.put(key, new PrefSlider(key, value, valueLow, valueHigh, dragStep, sliderX, sliderY, sliderW, sliderH, saves));
		sliderY += sliderSpacing;
	}
	
	public void addSliderVector(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves) {
		addSlider(key + "_X", value, valueLow, valueHigh, dragStep, saves);
		addSlider(key + "_Y", value, valueLow, valueHigh, dragStep, saves);
		addSlider(key + "_Z", value, valueLow, valueHigh, dragStep, saves);
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
	
	public String toJSON() {
		JSONObject json = new JSONObject();
		for (PrefSlider prefSlider : prefSliders.values()) {
			json.setFloat(prefSlider.key(), prefSlider.value());
		}
		return json.toString();
	}
	
}