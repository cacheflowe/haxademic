package com.haxademic.core.ui;

import processing.core.PGraphics;

public interface IUIControl {
	public static final String TYPE_TITLE = "title";
	public static final String TYPE_SLIDER = "slider";
	public static final String TYPE_BUTTON = "button";
	
	public static int TEXT_INDENT = 6;
	
	public String type();
	public String id();
	public void set(float val);
	public float value();
	public float valueEased();
	public float step();
	public float valueMin();
	public float valueMax();
	public float toggles();
	public float layoutW();
	public void layoutW(float val);
	public void update();
	public void draw(PGraphics pg);
}
