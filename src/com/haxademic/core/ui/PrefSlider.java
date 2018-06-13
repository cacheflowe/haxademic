package com.haxademic.core.ui;

import java.awt.Point;
import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.file.PrefToText;

import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class PrefSlider {
	
	protected String property;
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

	public PrefSlider(String property, float value, float low, float high, float dragStep, int x, int y, int w, int h) {
		this(property, value, low, high, dragStep, x, y, w, h, true);
	}
	
	public PrefSlider(String property, float value, float low, float high, float dragStep, int x, int y, int w, int h, boolean saves) {
		this.property = property;
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
	
	public float value() {
		return value;
	}
	
	public void update(PGraphics pg) {
		// background
		pg.fill(50);
		pg.noStroke();
		pg.rect(x, y, w, h);
		// text label
		pg.fill(255);
		pg.textSize(12);
		pg.text(property + ": " + value, x + 4, y + 3, w, 20);
		uiRect.setBounds(x, y, w, h);
		// outline
		pg.strokeWeight(1);
		if(mouseHovered) pg.stroke(0,255,0);
		else pg.stroke(0,0,255);
		pg.noFill();
		pg.rect(x, y, w, h);
		// draw current value
		float mappedX = P.map(value, low, high, x, x + w);
		pg.rect(mappedX - 0.5f, y, 1, h);
		// set active if drawing
		activeTime = P.p.millis();
	}
	
	public boolean isActive() {
		return (P.p.millis() - activeTime) < 10; // when drawing, time is tracked. if not drawing, time will be out-of-date
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
				if(saves) PrefToText.setValue(property, value);
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
		}
	}
	

}
