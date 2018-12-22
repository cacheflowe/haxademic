package com.haxademic.core.hardware.keyboard;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.shared.InputState;

import processing.event.KeyEvent;

public class KeyboardState {

	protected HashMap<Integer, InputState> keyboardButtons = new HashMap<Integer, InputState>();
	protected int lastUpdatedFrame = 0;

	public KeyboardState() {
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
		
	public void update() {
		if(P.p.frameCount == lastUpdatedFrame) return; 
		for (Integer key : keyboardButtons.keySet()) {
			if(keyboardButtons.get(key) == InputState.TRIGGER) keyboardButtons.put(key, InputState.ON);
		}
	}
	
	///////////////////////////////
	// PUBLIC GETTERS
	///////////////////////////////
	
	public boolean isKeyTriggered(int keyCode) {
		return (keyboardButtons.containsKey(keyCode) && keyboardButtons.get(keyCode) == InputState.TRIGGER);
	}
	
	public boolean isKeyOn(int keyCode) {
		return (keyboardButtons.containsKey(keyCode) && (keyboardButtons.get(keyCode) == InputState.TRIGGER || keyboardButtons.get(keyCode) == InputState.ON));
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
		keyboardButtons.put(keyCode, InputState.TRIGGER);
		lastUpdatedFrame = P.p.frameCount;
	}

	public void setKeyOff(int keyCode) {
		keyboardButtons.put(keyCode, InputState.OFF);
		lastUpdatedFrame = P.p.frameCount;
	}
	
	///////////////////////////////
	// DEBUG
	///////////////////////////////
	
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
