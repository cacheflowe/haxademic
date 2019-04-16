package com.haxademic.core.hardware.midi.devices;

import com.haxademic.core.app.P;

public class LaunchPad {
	
	public static int gridMidiNote(int x, int y) {
		return y * 16 + x;
	}
	
	public static int headerColMidiNote(int x) {
		return 104 + x;
	}
	
	public static int groupRowMidiNote(int y) {
		return y * 16 + 8;
	}
	
	public static int xFromNote(int note) {
		return note % 16;
	}

	public static int yFromNote(int note) {
		return P.floor(note / 16);
	}
	
}
