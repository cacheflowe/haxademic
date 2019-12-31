package com.haxademic.core.ui;

import java.awt.Rectangle;

import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.math.MathUtil;

import processing.core.PGraphics;

public class UITitle
implements IUIControl {
	
	protected String id;
	protected String label;
	protected Rectangle rect;
	protected float value = 0;
	protected float layoutW = 1;

	public UITitle(String title, int x, int y, int w, int h) {
		this.id = title + "_" + MathUtil.randRange(10000, 99999);
		this.label = title;
		rect = new Rectangle(x, y, w, h);
		layoutW = 1;
	}
	
	/////////////////////////////////////////
	// IUIControl interface
	/////////////////////////////////////////
	
	public String type() {
		return IUIControl.TYPE_TITLE;
	}
	
	public String id() {
		return id;
	}
	
	public void label(String label) {
		this.label = label;
	}
	
	public String label() {
		return label;
	}
	
	public void setPosition(int x, int y) {
		rect.setLocation(x, y);
	}
	
	public float value() {
		return value;
	}
	
	public float valueEased() {
		return value;
	}
	
	public float valueMin() {
		return 0;
	}
	
	public float valueMax() {
		return 1;
	}
	
	public float step() {
		return 1;
	}
	
	public float toggles() {
		return 0;
	}
	
	public float layoutW() {
		return layoutW;
	}
	
	public void layoutW(float val) {
		layoutW = val;
	}
	
	public void set(float val) {
		value = val;
	}
	
	public void update() {
		
	}
	
	public void draw(PGraphics pg) {
		PG.setDrawCorner(pg);

		// outline
		pg.noStroke();
		pg.fill(ColorsHax.BUTTON_OUTLINE);
		pg.rect(rect.x, rect.y, rect.width, rect.height);

		// background
		pg.fill(ColorsHax.TITLE_BG);
		pg.rect(rect.x+1, rect.y+1, rect.width-2, rect.height-2);

		// text label
		IUIControl.setFont(pg);
		pg.fill(ColorsHax.BUTTON_TEXT);
		pg.text(label, rect.x + TEXT_INDENT, rect.y);
	}
	
}
