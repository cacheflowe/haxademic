package com.haxademic.core.hardware.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.gamepad.GamepadState;
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
	
	public InputTrigger() {
		this( null, null, null );
	}
	
	public InputTrigger( char[] charList ) {
		this( charList, null, null );
	}
	
	public InputTrigger( char[] charList, Integer[] midiNotes ) {
		this( charList, null, midiNotes );
	}
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes ) {
		if(charList != null) {
			keyCodes = new Integer[charList.length];
			for (int i = 0; i < charList.length; i++) keyCodes[i] = KeyCodes.keyCodeFromChar(charList[i]);
		}
		if(oscMessages != null) this.oscMessages = oscMessages;
		if(midiNotes != null) this.midiNotes = midiNotes;
	}
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes, Integer[] midiCCNotes ) {
		this( charList, oscMessages, midiNotes );
		if(midiCCNotes != null) this.midiCC = midiCCNotes;
	}
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes, Integer[] midiCCNotes, String[] webControls ) {
		this( charList, oscMessages, midiNotes );
		if(midiCCNotes != null) this.midiCC = midiCCNotes;
		if(webControls != null) this.webControls = webControls;
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
	
	public InputTrigger addWebControls(String[] webControls) {
		this.webControls = webControls; 
		return this;
	}
	
	// getters
	
	public float value() {
		return curValue;
	}
	
	public boolean triggered() {
		// if triggered, also store the latest value
		for( int i=0; i < keyCodes.length; i++ ) {
			if(KeyboardState.instance().isKeyTriggered(keyCodes[i])) return true;
		}
		if(P.p.oscState != null) {
			for( int i=0; i < oscMessages.length; i++ ) {
				if( P.p.oscState.isValueTriggered(oscMessages[i])) {
					curValue = P.p.oscState.getValue(oscMessages[i]);
					return true;
				}
			}
		}
		for( int i=0; i < webControls.length; i++ ) {
			if( P.p.browserInputState.isValueTriggered(webControls[i])) {
				curValue = P.p.browserInputState.getValue(webControls[i]);
				return true;
			}
		}
		for( int i=0; i < midiNotes.length; i++ ) {
			if(MidiState.instance().isMidiNoteTriggered(midiNotes[i])) {
				curValue = MidiState.instance().midiNoteValue(midiNotes[i]);
				return true;
			}
		}
		for( int i=0; i < midiCC.length; i++ ) {
			if(MidiState.instance().isMidiCCTriggered(midiCC[i])) {
				curValue = MidiState.instance().midiCCPercent(0, midiCC[i]);
				return true;
			}
		}
		for( int i=0; i < midiCC.length; i++ ) {
			if(MidiState.instance().isMidiCCTriggered(midiCC[i])) {
				curValue = MidiState.instance().midiCCPercent(0, midiCC[i]);
				return true;
			}
		}
		for( int i=0; i < gamepadControls.length; i++ ) {
			if(GamepadState.instance().isValueTriggered(gamepadControls[i])) {
				curValue = GamepadState.instance().getValue(gamepadControls[i]);
				return true;
			}
		}
		return false;
	}

	public boolean on() {
		if(keyCodes != null) {
			for( int i=0; i < keyCodes.length; i++ ) {
				if( KeyboardState.instance().isKeyOn(keyCodes[i]) ) return true;
			}
		}
		if(P.p.oscState != null) {
			for( int i=0; i < oscMessages.length; i++ ) {
				if( P.p.oscState.isValueOn(oscMessages[i])) return true;
			}
		}
		if(midiNotes != null) {
			for( int i=0; i < midiNotes.length; i++ ) {
				if( MidiState.instance().isMidiNoteOn(midiNotes[i])) return true;
			}
		}
		if(webControls != null) {
			for( int i=0; i < webControls.length; i++ ) {
				if( P.p.browserInputState.isValueOn(webControls[i])) return true;
			}
		}
		if(gamepadControls != null) {
			for( int i=0; i < gamepadControls.length; i++ ) {
				if( GamepadState.instance().isValueOn(gamepadControls[i])) return true;
			}
		}
		return false;
	}
	
}
