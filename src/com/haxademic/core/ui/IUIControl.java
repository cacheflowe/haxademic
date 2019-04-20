package com.haxademic.core.ui;

import processing.core.PGraphics;

public interface IUIControl {
	public String id();
	public void set(float val);
	public float value();
	public void update(PGraphics pg);
}
