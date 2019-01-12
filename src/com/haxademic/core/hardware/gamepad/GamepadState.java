package com.haxademic.core.hardware.gamepad;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.shared.InputState;

public class GamepadState {

	protected HashMap<String, Float> controlValues;
	protected HashMap<String, InputState> controlState;
	protected int lastUpdatedFrame = 0;
	
	public GamepadState() {
		controlValues = new HashMap<String, Float>();
		controlState = new HashMap<String, InputState>();
	}
	
	///////////////////////////////
	// PUBLIC INTERFACE
	///////////////////////////////

	public float getValue( String controlId ) {
		if( controlValues.containsKey( controlId ) ) {
			return controlValues.get(controlId);
		} else {
			return 0;
		}
	}
	
	public boolean isValueTriggered( String controlId ) {
		if( controlState.containsKey( controlId ) ) {
			return controlState.get(controlId) == InputState.TRIGGER;
		} else {
			return false;
		}
	}
	
	public boolean isValueOn( String controlId ) {
		if( controlValues.containsKey( controlId ) ) {
			return controlValues.get(controlId).floatValue() > 0.01;
		} else {
			return false;
		}
	}
	
	///////////////////////////////
	// INCOMING EVENT CALLBACK
	///////////////////////////////

	public void setControlValue(String controlId, float controlValue) {
		controlValues.put(controlId, controlValue);
		InputState newState = (controlValue == 0) ? InputState.OFF : InputState.TRIGGER;
		controlState.put(controlId, newState);
		lastUpdatedFrame = P.p.frameCount;
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
	
	public void update() {
		if(P.p.frameCount == lastUpdatedFrame) return; 
		for (String key : controlState.keySet()) {
			if(controlState.get(key) == InputState.TRIGGER) controlState.put(key, InputState.ON);
		}
	}

	///////////////////////////////
	// DEBUG
	///////////////////////////////

	public void printControls() {
		P.p.noStroke();
		P.p.fill(255);
		String debugStr = "";
		for (String key : controlValues.keySet()) {
			if(controlState.get(key) == InputState.TRIGGER) {
				debugStr += key + ": TRIGGER \n";
			} else {
				debugStr += key + ": " + controlValues.get(key) + "\n";
			}
		}
		P.p.text(debugStr, 420, 20, P.p.width - 40, P.p.height - 40);
	}
}
