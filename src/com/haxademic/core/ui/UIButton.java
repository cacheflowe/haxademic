package com.haxademic.core.ui;

import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.DemoAssets;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.MouseEvent;

public class UIButton
implements IUIControl {
	
	public interface IUIButtonDelegate {
		public void clicked(UIButton button);
	}
	 
	protected IUIButtonDelegate delegate;
	protected String id;
	protected Rectangle rect;
	protected boolean over;
	protected boolean pressed;
	protected boolean toggles = false;
	protected float value = 0;
	protected float layoutW = 1;
	protected int activeTime = 0;

	public UIButton(IUIButtonDelegate delegate, String id, int x, int y, int w, int h, boolean toggles) {
		this.delegate = delegate;
		this.id = id;
		rect = new Rectangle( x, y, w, h );
		this.toggles = toggles;
		layoutW = 1;
		over = false;
		pressed = false;
		P.p.registerMethod("mouseEvent", this); // add mouse listeners
	}
	
	/////////////////////////////////////////
	// Disable/enable
	/////////////////////////////////////////
	
	public boolean isActive() {
		return (P.p.millis() - activeTime) < 10; // when drawing, time is tracked. if not drawing, time will be out-of-date
	}
	
	/////////////////////////////////////////
	// IUIControl interface
	/////////////////////////////////////////
	
	public String type() {
		return IUIControl.TYPE_BUTTON;
	}
	
	public String id() {
		return id;
	}
	
	public float value() {
		return value;
	}
	
	public float min() {
		return 0;
	}
	
	public float max() {
		return 1;
	}
	
	public float step() {
		return 1;
	}
	
	public float toggles() {
		return toggles ? 1 : 0;
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
	
	public void update(PGraphics pg) {
		DrawUtil.setDrawCorner(pg);

		// background
		if(over && value == 0) pg.fill(ColorsHax.BUTTON_BG_HOVER);
		else if(pressed) pg.fill(ColorsHax.BUTTON_BG_PRESS);
		else if(toggles && value == 1) pg.fill(ColorsHax.BUTTON_BG_SELECTED);
		else pg.fill(ColorsHax.BUTTON_BG);
		pg.noStroke();
		pg.rect(rect.x, rect.y, rect.width, rect.height);

		// outline
		pg.strokeWeight(1);
		if(over || pressed) pg.stroke(ColorsHax.BUTTON_OUTLINE_HOVER);
		else pg.stroke(ColorsHax.BUTTON_OUTLINE);
		pg.noFill();
		pg.rect(rect.x, rect.y, rect.width, rect.height);
		
		// text label
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, rect.height * 0.65f);
		FontCacher.setFontOnContext(pg, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		pg.fill(ColorsHax.BUTTON_TEXT);
		pg.text(id, rect.x + 4, rect.y + 0, rect.width, 20);
//		pg.text(key + ": " + value);
		
		// set active if drawing
		activeTime = P.p.millis();
	}
	
	/////////////////////////////////////////
	// Mouse listener
	/////////////////////////////////////////

	public void mouseEvent(MouseEvent event) {
		if(!isActive()) return;
		
		int mouseX = event.getX();
		int mouseY = event.getY();

		switch (event.getAction()) {
			case MouseEvent.PRESS:
				pressed = rect.contains(mouseX, mouseY);
				break;
			case MouseEvent.RELEASE:
				if(pressed && over) {
					if(toggles) {
						value = (value == 0) ? 1 : 0;
					}
					delegate.clicked(this);
				}
				pressed = false;
				break;
			case MouseEvent.MOVE:
				over = rect.contains(mouseX, mouseY);
				break;
			case MouseEvent.DRAG:
				over = rect.contains(mouseX, mouseY);
				break;
		}
	}
	
}
