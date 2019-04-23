package com.haxademic.core.ui;

import processing.core.PGraphics;

public interface IUIControl {
	public static final String TYPE_SLIDER = "slider";
	public static final String TYPE_BUTTON = "button";
	
	public String type();
	public String id();
	public void set(float val);
	public float value();
	public float step();
	public float min();
	public float max();
	public float toggles();
	public float layoutW();
	public void layoutW(float val);
	public void update(PGraphics pg);
}
