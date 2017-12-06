package com.haxademic.core.hardware.keyboard;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.shared.ButtonState;

import processing.event.KeyEvent;
import themidibus.MidiListener;

public class KeyboardState {

	public static boolean DEBUG = true;
		
	protected HashMap<Integer, ButtonState> keyboardButtons = new HashMap<Integer, ButtonState>();

	public KeyboardState() {
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
		
	public void update() {
		for (Integer key : keyboardButtons.keySet()) {
			if(keyboardButtons.get(key) == ButtonState.TRIGGER) keyboardButtons.put(key, ButtonState.ON);
		}
	}
	
	///////////////////////////////
	// PUBLIC GETTERS
	///////////////////////////////
	
	public boolean isKeyTriggered(int keyCode) {
		return (keyboardButtons.containsKey(keyCode) && keyboardButtons.get(keyCode) == ButtonState.TRIGGER);
	}
	
	public boolean isKeyOn(int keyCode) {
		return (keyboardButtons.containsKey(keyCode) && (keyboardButtons.get(keyCode) == ButtonState.TRIGGER || keyboardButtons.get(keyCode) == ButtonState.ON));
	}
	
	
	///////////////////////////////
	// KEYBOARD LISTENER
	///////////////////////////////

	public void keyEvent(KeyEvent e) {
		if(e.getAction() == KeyEvent.PRESS) {
			setKeyOn(e.getKeyCode());
		}
		if(e.getAction() == KeyEvent.RELEASE) {
			setKeyOff(e.getKeyCode());
		}
	}
	
	public void setKeyOn(int keyCode) {
		keyboardButtons.put(keyCode, ButtonState.TRIGGER);
	}

	public void setKeyOff(int keyCode) {
		keyboardButtons.put(keyCode, ButtonState.OFF);
	}
	
	public void printKeys() {
		P.p.noStroke();
		P.p.fill(255);
		
		// keyboard
		String debugStr = "";
		for (Integer key : keyboardButtons.keySet()) {
			debugStr += key + ": " + keyboardButtons.get(key) + "\n";
		}
		P.p.text(debugStr, 20, 20, P.p.width - 40, P.p.height - 40);
	}
	
}
