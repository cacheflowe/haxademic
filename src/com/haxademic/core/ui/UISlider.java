package com.haxademic.core.ui;

import java.awt.Point;
import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PTextAlign;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.DrawUtil;
import com.haxademic.core.draw.text.FontCacher;
import com.haxademic.core.file.PrefToText;
import com.haxademic.core.media.DemoAssets;

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
	protected float layoutW;
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
		this.layoutW = 1;
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
	
	public String type() {
		return IUIControl.TYPE_SLIDER;
	}
	
	public String id() {
		return id;
	}
	
	public float value() {
		return value;
	}
	
	public float min() {
		return low;
	}
	
	public float max() {
		return high;
	}
	
	public float step() {
		return dragStep;
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
	
	public void update(PGraphics pg) {
		DrawUtil.setDrawCorner(pg);
		float cornerRadius = 5;
		
		// background
		if(mouseHovered) pg.fill(ColorsHax.BUTTON_BG, 120);
		else pg.fill(ColorsHax.BUTTON_BG);
		pg.noStroke();
		pg.rect(x, y, w, h, cornerRadius);
		
		// text label
		PFont font = FontCacher.getFont(DemoAssets.fontOpenSansPath, h * 0.35f);
		FontCacher.setFontOnContext(pg, font, P.p.color(255), 1f, PTextAlign.CENTER, PTextAlign.CENTER);
		pg.fill(ColorsHax.BUTTON_TEXT);
		pg.text(id + ": " + value, x, y - 2, w, h);
		uiRect.setBounds(x, y, w, h);
		
		// outline
		pg.strokeWeight(1f);
		pg.stroke(ColorsHax.BUTTON_OUTLINE);
		pg.noFill();
		pg.rect(x, y, w, h, cornerRadius);
		
		// draw current value
		pg.noStroke();
		if(mousePressed) pg.fill(ColorsHax.WHITE, 200);
		else pg.fill(ColorsHax.WHITE, 90);
		float handleW = 30;
		float mappedX = P.map(value, low, high, x, x + w - handleW);
		pg.rect(mappedX - 0.5f, y, handleW, h, cornerRadius);
		
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
