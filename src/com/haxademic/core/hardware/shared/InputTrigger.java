package com.haxademic.core.hardware.shared;

import com.haxademic.core.app.P;


public class InputTrigger {
	
	protected char[] _keys;
	protected String[] _oscMessages;
	protected Integer[] _midiNotes;
	
	public InputTrigger( char[] charList, String[] oscMessages, Integer[] midiNote ) {
		_keys = charList;
		_oscMessages = oscMessages;
		_midiNotes = midiNote;
	}
	
	public boolean active() {
		for( int i=0; i < _keys.length; i++ ) {
			if( P.p.key == _keys[i] ) return true;
		}
		for( int i=0; i < _oscMessages.length; i++ ) {
			if( P.p._oscWrapper.oscMsgIsOn( _oscMessages[i] ) == 1 ) return true;
		}
		for( int i=0; i < _midiNotes.length; i++ ) {
			if( P.p.getMidi().midiNoteIsOn( _midiNotes[i] ) == 1 ) return true;
		}
		return false;
	}
}
