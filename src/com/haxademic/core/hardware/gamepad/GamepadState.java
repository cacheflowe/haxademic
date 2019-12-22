package com.haxademic.core.hardware.gamepad;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRegisterableMethods;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.hardware.shared.InputState;

public class GamepadState {

	protected GamepadListener gamepadListener;
	protected HashMap<String, Float> controlValues;
	protected HashMap<String, InputState> controlState;
	protected int lastUpdatedFrame = 0;
	
	
	public static GamepadState instance;
	
	public static GamepadState instance() {
		if(instance != null) return instance;
		instance = new GamepadState();
		return instance;
	}
	
	public GamepadState() {
		// hardware interface
		gamepadListener = new GamepadListener();
		
		// local state storage
		controlValues = new HashMap<String, Float>();
		controlState = new HashMap<String, InputState>();
		
		// update!
		P.p.registerMethod(PRegisterableMethods.pre, this);
		P.p.registerMethod(PRegisterableMethods.post, this);
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
		InputState newState = (controlValue == 0) ? InputState.OFF : InputState.TRIGGER;
		controlValues.put(controlId, controlValue);
		controlState.put(controlId, newState);
		lastUpdatedFrame = P.p.frameCount;
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
	
	public void pre() {
		logValues();
	}
	
	public void post() {
		if(P.p.frameCount == lastUpdatedFrame) return; 
		for (String key : controlState.keySet()) {
			if(controlState.get(key) == InputState.TRIGGER) controlState.put(key, InputState.ON);
		}
	}

	///////////////////////////////
	// DEBUG
	///////////////////////////////

	protected void logValues() {
		// debug print values if debug window is showing
		if(DebugView.active()) {
			for (String key : controlValues.keySet()) {
				DebugView.setValue("Gamepad ["+key+"]", controlValues.get(key) + " | " + controlState.get(key).name());
			}
		}
	}

}