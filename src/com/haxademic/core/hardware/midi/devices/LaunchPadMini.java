package com.haxademic.core.hardware.midi.devices;

import com.haxademic.core.app.P;

import themidibus.SimpleMidiListener;

public class LaunchPadMini
extends LaunchPad
implements SimpleMidiListener {
	
	public LaunchPadMini(String launchpadDeviceName) {
		super(launchpadDeviceName);
	}
	
	public LaunchPadMini(int midiIndexIn, int midiIndexOut) {
		super(midiIndexIn, midiIndexOut);
	}

	//////////////////////////////
	// STATIC GRID HELPERS
	//////////////////////////////
	
	public int colorByPercent(float percent) {
		return NovationColorsMK3.colorByPercent(percent);
	}
	
	public int numColors() {
		return NovationColorsMK3.colors.length;
	}
	
	public int gridMidiNote(int x, int y) {
		return LaunchPadMini.gridToMidiNote(x, y);
	}
	
	public static int gridToMidiNote(int x, int y) {
		if(x < 4) {
			return 36 + (7 - y) * 4 + x;
		} else {
			return 68 + (7 - y) * 4 + (x - 4);
		}
	}
	
//	public int headerColMidiNote(int x) {
//		return 104 + x;
//	}
//	
//	public int groupRowMidiNote(int y) {
//		return y * 16 + 8;
//	}
	
	public int xFromNote(int note) {
		return xIndexFromNote(note);
	}

	public static int xIndexFromNote(int note) {
		if(note < 68) {
			return (note - 36) % 4;
		} else {
			return (note - 68) % 4 + 4;
		}
	}
	
	public int yFromNote(int note) {
		return yIndexFromNote(note);
	}
	
	public static int yIndexFromNote(int note) {
		if(note < 68) {
			return 7 - P.floor((note - 36) / 4);
		} else {
			return 7 - P.floor((note - 68) / 4);
		}
	}
	
}
