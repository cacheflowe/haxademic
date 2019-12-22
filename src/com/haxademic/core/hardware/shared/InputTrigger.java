package com.haxademic.core.hardware.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.gamepad.GamepadState;
import com.haxademic.core.hardware.http.HttpInputState;
import com.haxademic.core.hardware.keyboard.KeyCodes;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.midi.MidiState;


public class InputTrigger {
	
	protected Integer[] keyCodes = new Integer[] {};
	protected String[] oscMessages = new String[] {};
	protected String[] webControls = new String[] {};
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
		this.webControls = webControls; 
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
			if(KeyboardState.instance().isKeyTriggered(keyCodes[i])) foundTrigger = true;
		}
		if(P.p.oscState != null) {
			if(foundTrigger == false) for( int i=0; i < oscMessages.length; i++ ) {
				if( P.p.oscState.isValueTriggered(oscMessages[i])) {
					curValue = P.p.oscState.getValue(oscMessages[i]);
					foundTrigger = true;
				}
			}
		}
		if(foundTrigger == false) for( int i=0; i < webControls.length; i++ ) {
			if(HttpInputState.instance().isValueTriggered(webControls[i])) {
				curValue = HttpInputState.instance().getValue(webControls[i]);
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
		if(keyCodes != null) {
			for( int i=0; i < keyCodes.length; i++ ) {
				if(KeyboardState.instance().isKeyOn(keyCodes[i])) return true;
			}
		}
		if(P.p.oscState != null) {
			for( int i=0; i < oscMessages.length; i++ ) {
				if( P.p.oscState.isValueOn(oscMessages[i])) return true;
			}
		}
		if(midiNotes != null) {
			for( int i=0; i < midiNotes.length; i++ ) {
				if(MidiState.instance().isMidiNoteOn(midiNotes[i])) return true;
			}
		}
		if(midiCC != null) {
			for( int i=0; i < midiCC.length; i++ ) {
				if(MidiState.instance().isMidiCCOn(midiCC[i])) return true;
			}
		}
		if(webControls != null) {
			for( int i=0; i < webControls.length; i++ ) {
				if(HttpInputState.instance().isValueOn(webControls[i])) return true;
			}
		}
		if(gamepadControls != null) {
			for( int i=0; i < gamepadControls.length; i++ ) {
				if(GamepadState.instance().isValueOn(gamepadControls[i])) return true;
			}
		}
		return false;
	}
	
}
