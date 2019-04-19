package com.haxademic.core.ui;

import processing.core.PGraphics;

public interface IMouseable {
	public void update(PGraphics pg, int mouseX, int mouseY);
	public String id();
}
