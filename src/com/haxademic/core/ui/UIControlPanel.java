package com.haxademic.core.ui;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.ui.UIButton.IUIButtonDelegate;

import processing.data.JSONObject;

public class UIControlPanel
implements IUIButtonDelegate {

	protected HashMap<String, IUIControl> controls;
	
	public static final int controlX = 10;
	protected int controlY = 10;
	public static final int controlW = 250;
	public static final int controlH = 14;
	public static final int controlSpacing = 18;
	protected float controlSpacingH = 4;

	protected boolean active = false;

	public UIControlPanel() {
		controls = new HashMap<String, IUIControl>();
	}
	
	////////////////////////
	// ADD controlS
	////////////////////////
	
	public void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep) {
		addSlider(key, value, valueLow, valueHigh, dragStep, true);
	}
	
	public void addSlider(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves) {
		controls.put(key, new UISlider(key, value, valueLow, valueHigh, dragStep, controlX, controlY, controlW, controlH, saves));
		controlY += controlSpacing;
	}
	
	public void addSliderVector(String key, float value, float valueLow, float valueHigh, float dragStep, boolean saves) {
		float controlWidthDivided = (controlW - controlSpacingH * 2f) / 3f;
		controls.put(key + "_X", new UISlider(key + "_X", value, valueLow, valueHigh, dragStep, P.round(controlX + 0 * controlWidthDivided + controlSpacingH * 0), controlY, P.round(controlWidthDivided), controlH, saves));
		controls.put(key + "_Y", new UISlider(key + "_Y", value, valueLow, valueHigh, dragStep, P.round(controlX + 1 * controlWidthDivided + controlSpacingH * 1), controlY, P.round(controlWidthDivided), controlH, saves));
		controls.put(key + "_Z", new UISlider(key + "_Z", value, valueLow, valueHigh, dragStep, P.round(controlX + 2 * controlWidthDivided + controlSpacingH * 2), controlY, P.round(controlWidthDivided), controlH, saves));
		controlY += controlSpacing;
	}
	
	public void removeControl(String key) {
		controls.remove(key);
	}
	
	////////////////////////
	// ADD BUTTONS
	////////////////////////
	
	public void addButton(String key, boolean toggles) {
		controls.put(key, new UIButton(this, key, controlX, controlY, controlW, controlH, toggles));
		controlY += controlSpacing;
	}
	
	public void addButtons(String[] keys, boolean toggles) {
		float controlWidthDivided = (controlW - controlSpacingH * (keys.length - 1)) / keys.length;
		for (int i = 0; i < keys.length; i++) {
			int buttonX = P.round(controlX + i * controlWidthDivided + controlSpacingH * i);
			controls.put(keys[i], new UIButton(this, keys[i], buttonX, controlY, P.round(controlWidthDivided), controlH, toggles));
		}
		controlY += controlSpacing;
	}
	
	////////////////////////
	// GET/SET VALUES
	////////////////////////
	
	public IUIControl get(String key) {
		return controls.get(key);
	}

	public void setValue(String key, float val) {
		controls.get(key).set(val);
	}
	
	public float value(String key) {
		return controls.get(key).value();
	}
	
	public int valueInt(String key) {
		return P.round(controls.get(key).value());
	}
	
	////////////////////////
	// DRAW/ACTIVATE/DEACTIVATE
	////////////////////////
	
	public void update() {
		if(!active) return;
		DrawUtil.setDrawFlat2d(P.p.g, true);
		for (IUIControl control : controls.values()) {
			control.update(P.p.g);
		}
		DrawUtil.setDrawFlat2d(P.p.g, false);
	}

	public void active(boolean val) {
		active = val;
	}

	public boolean active() {
		return active;
	}
	
	////////////////////////
	// EXPORT
	////////////////////////
	
	public String toJSON() {
		JSONObject json = new JSONObject();
		for (IUIControl control : controls.values()) {
			json.setFloat(control.id(), control.value());
		}
		return json.toString();
	}

	////////////////////////
	// IUIButtonDelegate
	////////////////////////
	
	public void clicked(UIButton button) {
		P.p.uiButtonClicked(button);
	}
	
}