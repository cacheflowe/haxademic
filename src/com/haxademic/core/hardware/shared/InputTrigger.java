package com.haxademic.core.hardware.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.keyboard.Keyboard;


public class InputTrigger {
	
	protected Integer[] keyCodes;
	protected String[] oscMessages;
	protected Integer[] midiNotes;
	protected Integer[] midiCC;
	protected float curValue = 0;
	
	public InputTrigger( char[] charList, Integer[] midiNotes ) {
		this( charList, null, midiNotes );
	}
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes ) {
		keyCodes = new Integer[charList.length];
		for (int i = 0; i < charList.length; i++) keyCodes[i] = Keyboard.keyCodeFromChar(charList[i]);
		this.oscMessages = oscMessages;
		this.midiNotes = midiNotes;
	}
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes, Integer[] midiCCNotes ) {
		this( charList, oscMessages, midiNotes );
		this.midiCC = midiCCNotes;
	}
	
	public float value() {
		return curValue;
	}
	
	public boolean triggered() {
		// if triggered, also store the latest value
		for( int i=0; i < keyCodes.length; i++ ) {
			if( P.p.keyboardState.isKeyTriggered(keyCodes[i]) ) return true;
		}
		if(P.p.oscWrapper != null) {
			for( int i=0; i < oscMessages.length; i++ ) {
				if( P.p.oscWrapper.isValueTriggered(oscMessages[i])) {
					curValue = P.p.oscWrapper.getValue(oscMessages[i]);
					return true;
				}
			}
		}
		for( int i=0; i < midiNotes.length; i++ ) {
			if( P.p.midi.isMidiButtonTriggered(midiNotes[i])) {
				curValue = P.p.midi.midiButtonValue(midiNotes[i]);
				return true;
			}
		}
		if(midiCC != null) {
			for( int i=0; i < midiCC.length; i++ ) {
				if( P.p.midi.isMidiCCTriggered(midiNotes[i])) {
					curValue = P.p.midi.midiCCPercent(0, midiNotes[i]);
					return true;
				}
			}
		}
		return false;
	}

	public boolean on() {
		for( int i=0; i < keyCodes.length; i++ ) {
			if( P.p.keyboardState.isKeyOn(keyCodes[i]) ) return true;
		}
		if(P.p.oscWrapper != null) {
			for( int i=0; i < oscMessages.length; i++ ) {
				if( P.p.oscWrapper.isValueOn(oscMessages[i])) return true;
			}
		}
		for( int i=0; i < midiNotes.length; i++ ) {
			if( P.p.midi.isMidiButtonOn(midiNotes[i])) return true;
		}
		return false;
	}
	
}
