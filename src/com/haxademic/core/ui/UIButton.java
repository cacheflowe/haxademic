package com.haxademic.core.ui;

import java.awt.Rectangle;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.draw.color.ColorsHax;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.hardware.midi.MidiState;

import processing.core.PGraphics;
import processing.event.MouseEvent;

public class UIButton
implements IUIControl {
	
	public interface IUIButtonDelegate {
		public void uiButtonClicked(UIButton button);
	}
	 
	protected IUIButtonDelegate delegate;
	protected String id;
	protected String label;
	protected Rectangle rect;
	protected boolean mouseHovered;
	protected boolean mousePressed;
	protected boolean toggles = false;
	protected float value = 0;
	protected float layoutW = 1;
	protected int activeTime = 0;
	protected int midiNote = -1;

	public UIButton(IUIButtonDelegate delegate, String id, int x, int y, int w, int h, boolean toggles) {
		this(delegate, id, x, y, w, h, toggles, -1);
	}
	
	public UIButton(IUIButtonDelegate delegate, String id, int x, int y, int w, int h, boolean toggles, int midiNote) {
		this.delegate = delegate;
		this.id = id;
		this.label = id;
		rect = new Rectangle( x, y, w, h);
		this.toggles = toggles;
		this.midiNote = midiNote;
		layoutW = 1;
		mouseHovered = false;
		mousePressed = false;
		P.p.registerMethod(PRegisterableMethods.mouseEvent, this); // add mouse listeners
	}
	
	protected void updateStore() {
		P.store.setNumber(id, value);
	}
	
	/////////////////////////////////////////
	// Disable/enable
	/////////////////////////////////////////
	
	public boolean isActive() {
		return (P.p.millis() - activeTime) < 100; // when drawing, time is tracked. if not drawing, time will be out-of-date
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
	
	public String valueString() {
		return value + "";
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
		updateStore();
	}
	
	public void set(String val) {
		// no-op
	}
	
	public boolean hovered() {
		return mouseHovered;
	}
	
	public void update() {
		// check midi
		if(midiNote != -1 && MidiState.instance().isMidiNoteTriggered(midiNote)) {
			// P.out("click framecount:", P.p.frameCount);
			click();
		}
	}
	
	public void draw(PGraphics pg) {
		PG.setDrawCorner(pg);

		// outline
		pg.noStroke();
		if(mouseHovered || mousePressed) pg.fill(ColorsHax.BUTTON_OUTLINE_HOVER);
		else pg.fill(ColorsHax.BUTTON_OUTLINE);
		pg.rect(rect.x, rect.y, rect.width, rect.height);

		// background
		if(mouseHovered && value == 0 && !mousePressed) pg.fill(ColorsHax.BUTTON_BG_HOVER);
		else if(mousePressed) pg.fill(ColorsHax.BUTTON_BG_PRESS);
		else if(toggles && value == 1) pg.fill(ColorsHax.WHITE);
		else pg.fill(ColorsHax.BUTTON_BG);
		pg.rect(rect.x+1, rect.y+1, rect.width-2, rect.height-2);

		// text label
		IUIControl.setFont(pg);
		if(toggles && value == 1) pg.fill(ColorsHax.BLACK);
		else pg.fill(ColorsHax.BUTTON_TEXT);
		pg.text(label, rect.x + TEXT_INDENT, rect.y, rect.width, rect.height);
		
		// set active if drawing
		activeTime = P.p.millis();
	}
	
	/////////////////////////////////////////
	// Mouse listener
	/////////////////////////////////////////

	public void click() {
		if(toggles) {
			value = (value == 0) ? 1 : 0;	// flip toggle value
		}
		updateStore();
		if(delegate != null) delegate.uiButtonClicked(this);	// deprecated
	}
	
	public void mouseEvent(MouseEvent event) {
		if(!isActive()) return;
		
		int mouseX = event.getX();
		int mouseY = event.getY();

		switch (event.getAction()) {
			case MouseEvent.PRESS:
				mousePressed = rect.contains(mouseX, mouseY);
				break;
			case MouseEvent.RELEASE:
				if(mousePressed && mouseHovered) click();
				mousePressed = false;
				break;
			case MouseEvent.MOVE:
				mouseHovered = rect.contains(mouseX, mouseY);
				break;
			case MouseEvent.DRAG:
				mouseHovered = rect.contains(mouseX, mouseY);
				break;
		}
	}
	
}
