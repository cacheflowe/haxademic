package com.haxademic.core.hardware.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.keyboard.Keyboard;


public class InputTrigger {
	
	protected Integer[] keyCodes;
	protected String[] oscMessages;
	protected String[] webControls;
	protected Integer[] midiNotes;
	protected Integer[] midiCC;
	protected float curValue = 0;
	
	public InputTrigger( char[] charList, Integer[] midiNotes ) {
		this( charList, null, midiNotes );
	}
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes ) {
		if(charList != null) {
			keyCodes = new Integer[charList.length];
			for (int i = 0; i < charList.length; i++) keyCodes[i] = Keyboard.keyCodeFromChar(charList[i]);
		}
		this.oscMessages = oscMessages;
		this.midiNotes = midiNotes;
	}
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes, Integer[] midiCCNotes ) {
		this( charList, oscMessages, midiNotes );
		this.midiCC = midiCCNotes;
	}
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes, Integer[] midiCCNotes, String[] webControls ) {
		this( charList, oscMessages, midiNotes );
		this.midiCC = midiCCNotes;
		this.webControls = webControls;
	}
	
	public float value() {
		return curValue;
	}
	
	public boolean triggered() {
		// if triggered, also store the latest value
		if(keyCodes != null) {
			for( int i=0; i < keyCodes.length; i++ ) {
				if( P.p.keyboardState.isKeyTriggered(keyCodes[i]) ) return true;
			}
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
				P.println("found WebControl:",webControls[i], P.p.browserInputState.getValue(webControls[i]));
				curValue = P.p.browserInputState.getValue(webControls[i]);
				return true;
			}
		}
		for( int i=0; i < midiNotes.length; i++ ) {
			if( P.p.midiState.isMidiButtonTriggered(midiNotes[i])) {
				curValue = P.p.midiState.midiButtonValue(midiNotes[i]);
				return true;
			}
		}
		if(midiCC != null) {
			for( int i=0; i < midiCC.length; i++ ) {
				if( P.p.midiState.isMidiCCTriggered(midiNotes[i])) {
					curValue = P.p.midiState.midiCCPercent(0, midiNotes[i]);
					return true;
				}
			}
		}
		return false;
	}

	public boolean on() {
		if(keyCodes != null) {
			for( int i=0; i < keyCodes.length; i++ ) {
				if( P.p.keyboardState.isKeyOn(keyCodes[i]) ) return true;
			}
		}
		if(P.p.oscState != null) {
			for( int i=0; i < oscMessages.length; i++ ) {
				if( P.p.oscState.isValueOn(oscMessages[i])) return true;
			}
		}
		for( int i=0; i < midiNotes.length; i++ ) {
			if( P.p.midiState.isMidiButtonOn(midiNotes[i])) return true;
		}
		return false;
	}
	
}
