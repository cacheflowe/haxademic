package com.haxademic.core.ui;

import java.awt.Point;
import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.file.PrefToText;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class UISlider
implements IUIControl {
	
	protected String id;
	protected float value;
	protected EasingFloat valueEased;
	protected float valueMin;
	protected float valueMax;
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
	protected int midiCCNote = -1;

	public UISlider(String property, float value, float low, float high, float dragStep, int x, int y, int w, int h) {
		this(property, value, low, high, dragStep, x, y, w, h, true);
	}
	
	public UISlider(String property, float value, float low, float high, float dragStep, int x, int y, int w, int h, boolean saves) {
		this(property, value, low, high, dragStep, x, y, w, h, true, -1);
	}

	public UISlider(String property, float value, float low, float high, float dragStep, int x, int y, int w, int h, boolean saves, int midiCCNote) {
		this.id = property;
		this.value = (saves) ? PrefToText.getValueF(property, value) : value;
		this.valueMin = low;
		this.valueMax = high;
		this.dragStep = dragStep;
		this.layoutW = 1;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.saves = saves;
		this.midiCCNote = midiCCNote;
		valueEased = new EasingFloat(this.value, 0.2f);
		P.p.registerMethod(PRegisterableMethods.mouseEvent, this);
		P.p.registerMethod(PRegisterableMethods.keyEvent, this);
	}
	
	/////////////////////////////////////////
	// Disable/enable
	/////////////////////////////////////////
	
	public boolean isActive() {
		return (P.p.millis() - activeTime) < 50; // when drawing, time is tracked. if not drawing, time will be out-of-date
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
	
	public float valueEased() {
		return valueEased.value();
	}
	
	public float valueMin() {
		return valueMin;
	}
	
	public float valueMax() {
		return valueMax;
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
	
	public void update() {
		// check midi
		if(midiCCNote != -1 && MidiState.instance().isMidiCCTriggered(midiCCNote)) {
			float val = MidiState.instance().midiCCNormalized(midiCCNote);
			set(P.map(val, 0, 1, valueMin, valueMax));
		}
		
		// do interpolation
		valueEased.setTarget(value);
		valueEased.update(true);
	}
	
	public void draw(PGraphics pg) {
		pg.pushMatrix();
		PG.setDrawCorner(pg);
		
		// outline
		pg.noStroke();
		pg.fill(ColorsHax.BUTTON_OUTLINE);
		pg.rect(x, y, w, h);
		
		// background
		if(mouseHovered) pg.fill(ColorsHax.BUTTON_BG_HOVER);
		else pg.fill(ColorsHax.BUTTON_BG);
		pg.rect(x+1, y+1, w-2, h-2);
		
		// draw current value
		pg.noStroke();
		if(mousePressed) pg.fill(ColorsHax.WHITE, 180);
		else pg.fill(0, 127, 0);
//		else pg.fill(ColorsHax.WHITE, 120);
		float handleW = 4;
		float mappedX = P.map(value, valueMin, valueMax, x+1, x + w - handleW);
		pg.rect(mappedX - 0.5f, y+1, handleW, h-2);
		
		// text label
		IUIControl.setFont(pg);
		pg.fill(ColorsHax.BUTTON_TEXT);
		pg.text(id + ": " + MathUtil.roundToPrecision(value, 5), P.round(x + TEXT_INDENT), y, w, h*2);
		uiRect.setBounds(x, y, w, h);
		
		// set active if drawing
		activeTime = P.p.millis();
		pg.popMatrix();
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
				value = P.constrain(value, valueMin, valueMax);
			}
			break;
		}
	}

	/////////////////////////////////////////
	// Keyboard events
	/////////////////////////////////////////
	
	public void keyEvent(KeyEvent e) {
		if(isActive() == false) return;
		if(mouseHovered == false) return;
		if(e.getAction() == KeyEvent.PRESS) {
			if(e.getKeyCode() == P.LEFT) { value -= dragStep; value = P.max(value, valueMin); }
			if(e.getKeyCode() == P.RIGHT) { value += dragStep; value = P.min(value, valueMax); }
			if(saves) PrefToText.setValue(id, value);
		}
	}

}
