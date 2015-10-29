package com.haxademic.core.hardware.shared;

import com.haxademic.core.app.P;


public class InputTrigger {
	
	protected char[] _keys;
	protected String[] _oscMessages;
	protected Integer[] _midiNotes;
	protected Integer[] _midiCC;
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes ) {
		_keys = charList;
		_oscMessages = oscMessages;
		_midiNotes = midiNotes;
	}
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNotes, Integer[] midiCCNotes ) {
		this( charList, oscMessages, midiNotes );
		_midiCC = midiCCNotes;
	}
	
	public boolean active() {
		for( int i=0; i < _keys.length; i++ ) {
			if( P.p.key == _keys[i] ) return true;
		}
		if(P.p._oscWrapper != null) {
			for( int i=0; i < _oscMessages.length; i++ ) {
				if( P.p._oscWrapper.oscMsgIsOn( _oscMessages[i] ) == 1 ) return true;
			}
		}
		for( int i=0; i < _midiNotes.length; i++ ) {
			if( P.p._midi.midiNoteIsOn( _midiNotes[i] ) == 1 ) return true;
		}
		for( int i=0; i < _midiNotes.length; i++ ) {
			if( P.p._midi.midiNoteIsOn( _midiNotes[i] ) == 1 ) return true;
		}
		return false;
	}
}
