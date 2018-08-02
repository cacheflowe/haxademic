package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.constants.AppSettings;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.midi.devices.LaunchPad;
import com.haxademic.core.hardware.midi.devices.NovationColors;

public class Demo_LaunchPad
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void overridePropsFile() {
		p.appConfig.setProperty(AppSettings.MIDI_DEVICE_IN_INDEX, 0 );
		p.appConfig.setProperty(AppSettings.MIDI_DEVICE_OUT_INDEX, 1 );
	}
	
	protected int gridMidiNote(int x, int y) {
		return y * 16 + x;
	}
	
	protected int headerColMidiNote(int x) {
		return 104 + x;
	}
	
	protected int groupRowMidiNote(int y) {
		return y * 16 + 8;
	}
	
	public void drawApp() {
		p.background(0);
		// p.midi.isMidiButtonOn(LaunchControl.PAD_01)
		// print debug
		p.midiState.printButtons();
		p.midiState.printCC();
		
		// outgoing midi changes the LED color on the launch control
		p.midiState.sendMidiOut(true, 0, gridMidiNote(0, 0), NovationColors.colors[P.round((NovationColors.colors.length - 1) * P.p.audioFreq(1) )]);
		p.midiState.sendMidiOut(true, 0, gridMidiNote(1, 1), NovationColors.colors[P.round((NovationColors.colors.length - 1) * P.p.audioFreq(100) )]);
		p.midiState.sendMidiOut(true, 0, gridMidiNote(2, 4), NovationColors.colors[P.round((NovationColors.colors.length - 1) * P.p.audioFreq(131) )]);
		p.midiState.sendMidiOut(true, 0, groupRowMidiNote(7), P.round(P.p.audioFreq(106) * 150f));
		p.midiState.sendMidiOut(true, 0, groupRowMidiNote(8), P.round(P.p.audioFreq(120) * 150f));
		p.midiState.sendMidiOut(true, 0, headerColMidiNote(0), P.round(P.p.audioFreq(105) * 150f));
		p.midiState.sendMidiOut(true, 0, headerColMidiNote(1), P.round(P.p.audioFreq(107) * 150f));
	}
	
}