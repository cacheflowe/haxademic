package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchPad;
import com.haxademic.core.hardware.midi.devices.NovationColors;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class Demo_LaunchPad
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void firstFrame() {
		AudioIn.instance();
//		MidiDevice.init(1, 4);
		MidiDevice.init("4- Launchpad");
	}
	
	protected void drawApp() {
		p.background(0);
		// p.midi.isMidiButtonOn(LaunchControl.PAD_01)
		
		// outgoing midi changes the LED color on the launch control
		MidiDevice.instance().sendMidiOut(true, 0, LaunchPad.gridMidiNote(0, 0), NovationColors.colors[P.round((NovationColors.colors.length - 1) * AudioIn.audioFreq(1) )]);
		MidiDevice.instance().sendMidiOut(true, 0, LaunchPad.gridMidiNote(1, 1), NovationColors.colors[P.round((NovationColors.colors.length - 1) * AudioIn.audioFreq(100) )]);
		MidiDevice.instance().sendMidiOut(true, 0, LaunchPad.gridMidiNote(2, 4), NovationColors.colors[P.round((NovationColors.colors.length - 1) * AudioIn.audioFreq(131) )]);
		MidiDevice.instance().sendMidiOut(true, 0, LaunchPad.groupRowMidiNote(7), P.round(AudioIn.audioFreq(106) * 150f));
		MidiDevice.instance().sendMidiOut(true, 0, LaunchPad.groupRowMidiNote(8), P.round(AudioIn.audioFreq(120) * 150f));
		MidiDevice.instance().sendMidiOut(true, 0, LaunchPad.headerColMidiNote(0), P.round(AudioIn.audioFreq(105) * 150f));
		MidiDevice.instance().sendMidiOut(true, 0, LaunchPad.headerColMidiNote(1), P.round(AudioIn.audioFreq(107) * 150f));
	}
	
}