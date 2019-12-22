package com.haxademic.core.hardware.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.gamepad.GamepadState;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.hardware.keyboard.KeyCodes;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.hardware.osc.OscState;


public class InputTrigger {
	
	protected Integer[] keyCodes = new Integer[] {};
	protected String[] oscMessages = new String[] {};
	protected String[] httpRequests = new String[] {};
	protected String[] gamepadControls = new String[] {};
	protected Integer[] midiNotes = new Integer[] {};
	protected Integer[] midiCC = new Integer[] {};
	protected float curValue = 0;
	protected String broadcastKey;
	
	public InputTrigger() {
		
	}
	
	// chainable setters
	
	public InputTrigger addKeyCodes(char[] charList) {
		keyCodes = new Integer[charList.length];
		for (int i = 0; i < charList.length; i++) keyCodes[i] = KeyCodes.keyCodeFromChar(charList[i]);
		return this;
	}
	
	public InputTrigger addMidiNotes(Integer[] midiNotes) {
		this.midiNotes = midiNotes; 
		return this;
	}
	
	public InputTrigger addMidiCCNotes(Integer[] midiCCNotes) {
		this.midiCC = midiCCNotes; 
		return this;
	}
	
	public InputTrigger addOscMessages(String[] oscMessages) {
		this.oscMessages = oscMessages; 
		return this;
	}
	
	public InputTrigger addGamepadControls(String[] gamepadControls) {
		this.gamepadControls = gamepadControls; 
		return this;
	}
	
	public InputTrigger addHttpRequests(String[] webControls) {
		this.httpRequests = webControls; 
		return this;
	}
	
	public InputTrigger setBroadcastKey(String broadcastKey) {
		this.broadcastKey = broadcastKey;
		return this;
	}
	
	// getters
	
	public float value() {
		return curValue;
	}
	
	public boolean triggered() {
		boolean foundTrigger = false;
		// if triggered, also store the latest value
		if(foundTrigger == false) for( int i=0; i < keyCodes.length; i++ ) {
			if(KeyboardState.instance().isKeyTriggered(keyCodes[i])) {
				curValue = 1;
				foundTrigger = true;
			}
		}
		if(foundTrigger == false) for( int i=0; i < oscMessages.length; i++ ) {
			if(OscState.instance().isValueTriggered(oscMessages[i])) {
				curValue = OscState.instance().getValue(oscMessages[i]);
				foundTrigger = true;
			}
		}
		if(foundTrigger == false) for( int i=0; i < httpRequests.length; i++ ) {
			if(HttpInputState.instance().isValueTriggered(httpRequests[i])) {
				curValue = HttpInputState.instance().getValue(httpRequests[i]);
				foundTrigger = true;
			}
		}
		if(foundTrigger == false) for( int i=0; i < midiNotes.length; i++ ) {
			if(MidiState.instance().isMidiNoteTriggered(midiNotes[i])) {
				curValue = MidiState.instance().midiNoteValue(midiNotes[i]);
				foundTrigger = true;
			}
		}
		if(foundTrigger == false) for( int i=0; i < midiCC.length; i++ ) {
			if(MidiState.instance().isMidiCCTriggered(midiCC[i])) {
				curValue = MidiState.instance().midiCCPercent(0, midiCC[i]);
				foundTrigger = true;
			}
		}
		if(foundTrigger == false) for( int i=0; i < gamepadControls.length; i++ ) {
			if(GamepadState.instance().isValueTriggered(gamepadControls[i])) {
				curValue = GamepadState.instance().getValue(gamepadControls[i]);
				foundTrigger = true;
			}
		}
		
		// return state
		if(foundTrigger) {
			// send it out to AppStore if needed
			if(broadcastKey != null) P.store.setNumber(broadcastKey, curValue);
			// return triggered state! 
			return true;
		}
		return false;
	}

	public boolean on() {
		for( int i=0; i < keyCodes.length; i++ ) {
			if(KeyboardState.instance().isKeyOn(keyCodes[i])) return true;
		}
		for( int i=0; i < oscMessages.length; i++ ) {
			if(OscState.instance().isValueOn(oscMessages[i])) return true;
		}
		for( int i=0; i < midiNotes.length; i++ ) {
			if(MidiState.instance().isMidiNoteOn(midiNotes[i])) return true;
		}
		for( int i=0; i < midiCC.length; i++ ) {
			if(MidiState.instance().isMidiCCOn(midiCC[i])) return true;
		}
		for( int i=0; i < httpRequests.length; i++ ) {
			if(HttpInputState.instance().isValueOn(httpRequests[i])) return true;
		}
		for( int i=0; i < gamepadControls.length; i++ ) {
			if(GamepadState.instance().isValueOn(gamepadControls[i])) return true;
		}
		
		// switched to no longer being active
		if(curValue > 0) {
			curValue = 0;
			if(broadcastKey != null) P.store.setNumber(broadcastKey, curValue);
		}
		return false;
	}
	
}
