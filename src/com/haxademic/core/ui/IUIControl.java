package com.haxademic.core.ui;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.media.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;

public interface IUIControl {
	public static final String TYPE_TITLE = "title";
	public static final String TYPE_SLIDER = "slider";
	public static final String TYPE_BUTTON = "button";
	public static final String TYPE_TEXTFIELD = "textfield";
	
	public static int TEXT_INDENT = 6;
	public static final int controlW = 250;
	public static final int controlH = 16;
	public static final int controlSpacing = controlH - 1;

	public static void setFont(PGraphics pg) {
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, 11);
		FontCacher.setFontOnContext(pg, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
	}
	
	public String type();
	public String id();
	public void set(float val);
	public void set(String val);
	public boolean hovered();
	public float value();
	public float valueEased();
	public String valueString();
	public float step();
	public float valueMin();
	public float valueMax();
	public float toggles();
	public float layoutW();
	public void layoutW(float val);
	public void update();
	public void draw(PGraphics pg);
}
