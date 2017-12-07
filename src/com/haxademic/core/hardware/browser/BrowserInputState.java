package com.haxademic.core.hardware.browser;

import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.shared.InputState;

public class BrowserInputState {

	protected HashMap<String, Float> webControlValues;
	protected HashMap<String, InputState> webControlState;
	
	public BrowserInputState() {
		webControlValues = new HashMap<String, Float>();
		webControlState = new HashMap<String, InputState>();
	}
	
	///////////////////////////////
	// PUBLIC INTERFACE
	///////////////////////////////

	public float getValue( String controlId ) {
		if( webControlValues.containsKey( controlId ) ) {
			return webControlValues.get(controlId);
		} else {
			return 0;
		}
	}
	
	public boolean isValueTriggered( String controlId ) {
		if( webControlState.containsKey( controlId ) ) {
			return webControlState.get(controlId) == InputState.TRIGGER;
		} else {
			return false;
		}
	}
	
	public boolean isValueOn( String controlId ) {
		if( webControlValues.containsKey( controlId ) ) {
			return webControlValues.get(controlId).floatValue() > 0;
		} else {
			return false;
		}
	}
	
	///////////////////////////////
	// INCOMING EVENT CALLBACK
	///////////////////////////////

	public void setControlValue(String controlId, float controlValue) {
		if(P.p.showDebug) P.println(controlId+": "+controlValue);
		webControlValues.put(controlId, controlValue);
		InputState newState = (controlValue == 0) ? InputState.OFF : InputState.TRIGGER;
		webControlState.put(controlId, newState);
	}
	
	///////////////////////////////
	// AUTO-SWITCH `TRIGGER` TO `ON`
	///////////////////////////////
	
	public void update() {
		for (String key : webControlState.keySet()) {
			if(webControlState.get(key) == InputState.TRIGGER) webControlState.put(key, InputState.ON);
		}
	}

	///////////////////////////////
	// DEBUG
	///////////////////////////////

	public void printButtons() {
		P.p.noStroke();
		P.p.fill(255);
		String debugStr = "";
		for (String key : webControlValues.keySet()) {
			if(webControlState.get(key) == InputState.TRIGGER) {
				debugStr += key + ": TRIGGER \n";
			} else {
				debugStr += key + ": " + webControlValues.get(key) + "\n";
			}
		}
		P.p.text(debugStr, 520, 20, P.p.width - 40, P.p.height - 40);
	}


}
