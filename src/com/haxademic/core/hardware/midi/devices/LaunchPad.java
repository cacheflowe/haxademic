package com.haxademic.core.hardware.midi.devices;

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

}
