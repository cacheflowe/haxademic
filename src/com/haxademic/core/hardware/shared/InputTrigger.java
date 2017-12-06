package com.haxademic.core.hardware.shared;

import com.haxademic.core.app.P;
import com.haxademic.core.hardware.keyboard.Keyboard;


public class InputTrigger {
	
	protected Integer[] _keys;
	protected String[] _oscMessages;
	protected Integer[] _midiNotes;
	protected Integer[] _midiCC;
	
	public InputTrigger( char[] charList, Integer[] midiNotes ) {
		this( charList, null, midiNotes );
	}
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes ) {
		_keys = new Integer[charList.length];
		for (int i = 0; i < charList.length; i++) {
			_keys[i] = Keyboard.keyCodeFromChar(charList[i]);
		}
		_oscMessages = oscMessages;
		_midiNotes = midiNotes;
	}
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes, Integer[] midiCCNotes ) {
		this( charList, oscMessages, midiNotes );
		_midiCC = midiCCNotes;
	}
	
	public boolean triggered() {
		for( int i=0; i < _keys.length; i++ ) {
			if( P.p.keyboardState.isKeyTriggered(_keys[i]) ) return true;
		}
		if(P.p.oscWrapper != null) {
			for( int i=0; i < _oscMessages.length; i++ ) {
				if( P.p.oscWrapper.isValueTriggered(_oscMessages[i])) return true;
			}
		}
		for( int i=0; i < _midiNotes.length; i++ ) {
			if( P.p.midi.isMidiButtonTriggered(_midiNotes[i])) return true;
		}
		return false;
	}

	public boolean on() {
		for( int i=0; i < _keys.length; i++ ) {
			if( P.p.keyboardState.isKeyOn(_keys[i]) ) return true;
		}
		if(P.p.oscWrapper != null) {
			for( int i=0; i < _oscMessages.length; i++ ) {
				if( P.p.oscWrapper.getValue(_oscMessages[i]) > 0 ) return true;
			}
		}
		for( int i=0; i < _midiNotes.length; i++ ) {
			if( P.p.midi.isMidiButtonOn(_midiNotes[i])) return true;
		}
		return false;
	}
}
