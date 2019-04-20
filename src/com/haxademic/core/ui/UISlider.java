package com.haxademic.core.ui;

import java.awt.Point;
import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.DemoAssets;
import com.haxademic.core.file.PrefToText;

import processing.core.PFont;
import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class UISlider
implements IUIControl {
	
	protected String id;
	protected float value;
	protected float low;
	protected float high;
	protected float dragStep;
	protected int x;
	protected int y;
	protected int w;
	protected int h;
	protected int activeTime = 0;
	protected Point mousePoint = new Point();
	protected Rectangle uiRect = new Rectangle();
	protected boolean mouseHovered = false;
	protected boolean mousePressed = false;
	protected boolean saves = false;

	public UISlider(String property, float value, float low, float high, float dragStep, int x, int y, int w, int h) {
		this(property, value, low, high, dragStep, x, y, w, h, true);
	}
	
	public UISlider(String property, float value, float low, float high, float dragStep, int x, int y, int w, int h, boolean saves) {
		this.id = property;
		this.value = (saves) ? PrefToText.getValueF(property, value) : value;
		this.low = low;
		this.high = high;
		this.dragStep = dragStep;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.saves = saves;
		P.p.registerMethod("mouseEvent", this);
		P.p.registerMethod("keyEvent", this);
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
	
	public String id() {
		return id;
	}
	
	public float value() {
		return value;
	}
	
	public void set(float val) {
		value = val;
	}
	
	public void update(PGraphics pg) {
		DrawUtil.setDrawCorner(pg);
		
		// background
		if(mouseHovered) pg.fill(ColorsHax.BUTTON_BG_HOVER);
		else pg.fill(ColorsHax.BUTTON_BG);
		pg.noStroke();
		pg.rect(x, y, w, h);
		
		// text label
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, h * 0.65f);
		FontCacher.setFontOnContext(pg, font, P.p.color(255), 1f, PTextAlign.LEFT, PTextAlign.TOP);
		pg.fill(ColorsHax.BUTTON_TEXT);
		pg.text(id + ": " + value, x + 4, y + 0, w, 20);
		uiRect.setBounds(x, y, w, h);
		
		// outline
		pg.strokeWeight(1);
		if(mouseHovered) pg.stroke(ColorsHax.BUTTON_OUTLINE_HOVER);
		else pg.stroke(ColorsHax.BUTTON_OUTLINE);
		pg.noFill();
		pg.rect(x, y, w, h);
		
		// draw current value
		pg.stroke(ColorsHax.BUTTON_TEXT);
		float mappedX = P.map(value, low, high, x, x + w);
		pg.rect(mappedX - 0.5f, y, 1, h);
		
		// set active if drawing
		activeTime = P.p.millis();
	}
	
	/////////////////////////////////////////
	// Mouse events
	/////////////////////////////////////////
	
	public void mouseEvent(MouseEvent event) {
		if(isActive() == false) return;
		// collision detection
		mousePoint.setLocation(event.getX(), event.getY());
		switch (event.getAction()) {
		case MouseEvent.PRESS:
			if(uiRect.contains(mousePoint)) mousePressed = true;
			break;
		case MouseEvent.RELEASE:
			if(mousePressed) {
				mousePressed = false;
				if(saves) PrefToText.setValue(id, value);
			}
			break;
		case MouseEvent.MOVE:
			mouseHovered = uiRect.contains(mousePoint);
			break;
		case MouseEvent.DRAG:
			if(mousePressed) {
				float deltaX = (P.p.mouseX - P.p.pmouseX) * dragStep;
				value += deltaX;
				value = P.constrain(value, low, high);
			}
			break;
		}
	}

	/////////////////////////////////////////
	// Keyboard events
	/////////////////////////////////////////
	
	public void keyEvent(KeyEvent e) {
		if(isActive() == false) return;
		if(mousePressed == false) return;
		if(e.getAction() == KeyEvent.PRESS) {
			if(e.getKeyCode() == P.LEFT) value -= dragStep;
			if(e.getKeyCode() == P.RIGHT) value += dragStep;
			if(saves) PrefToText.setValue(id, value);
		}
	}

}
