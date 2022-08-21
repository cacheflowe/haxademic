package com.haxademic.core.hardware.keyboard;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.shared.InputTrigger.InputState;

import processing.event.KeyEvent;

public class KeyboardState {

	protected HashMap<Integer, InputState> keyboardButtons = new HashMap<Integer, InputState>();
	protected int lastUpdatedFrame = 0;
	protected boolean updatesDebugView = false;

	// Singleton instance
	
	public static KeyboardState instance;
	
	public static KeyboardState instance() {
		if(instance != null) return instance;
		instance = new KeyboardState();
		return instance;
	}
	
	// Constructor

	public KeyboardState() {
		P.p.registerMethod(PRegisterableMethods.post, this);
		P.p.registerMethod(PRegisterableMethods.keyEvent, this);
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
		
	public void post() {
		logValues();
		if(P.p.frameCount == lastUpdatedFrame) return; 
		for (Integer key : keyboardButtons.keySet()) {
			if(keyboardButtons.get(key) == InputState.TRIGGER) keyboardButtons.put(key, InputState.ON);
		}
	}
	
	///////////////////////////////
	// PUBLIC GETTERS
	///////////////////////////////
	
	public static boolean keyTriggered(char key) {
		return KeyboardState.instance().isKeyTriggered(KeyCodes.keyCodeFromChar(key));
	}
	
	public static boolean keyTriggered(int keyCode) {
		return KeyboardState.instance().isKeyTriggered(keyCode);
	}
	
	public boolean isKeyTriggered(int key) {
		return (keyboardButtons.containsKey(key) && keyboardButtons.get(key) == InputState.TRIGGER);
	}
	
	public static boolean keyOn(char key) {
		return KeyboardState.instance().isKeyOn(KeyCodes.keyCodeFromChar(key));
	}
	
	public static boolean keyOn(int keyCode) {
		return KeyboardState.instance().isKeyOn(keyCode);
	}
	
	public boolean isKeyOn(int key) {
		return (keyboardButtons.containsKey(key) && (keyboardButtons.get(key) == InputState.TRIGGER || keyboardButtons.get(key) == InputState.ON));
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
	
	public void updatesDebugView(boolean updatesDebugView) {
		this.updatesDebugView = updatesDebugView;
	}
	
	protected void logValues() {
		if(!updatesDebugView) return;
		// debug print values if debug window is showing
		if(DebugView.active()) {
			for (Integer key : keyboardButtons.keySet()) {
				DebugView.setValue("Keyboard ["+key+"]", keyboardButtons.get(key).name());
			}
		}
	}

}
