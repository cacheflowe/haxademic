package com.haxademic.demo.hardware.midi;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.MidiState;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.media.audio.analysis.AudioIn;

import themidibus.SimpleMidiListener;

public class Demo_MidiBus
extends PAppletHax
implements SimpleMidiListener {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void setupFirstFrame() {
		AudioIn.instance();
		MidiDevice.init(0, 3, this);
	}
	
	public void drawApp() {
		p.background(0);
		// p.midi.isMidiButtonOn(LaunchControl.PAD_01)
		// print debug
		MidiState.instance().printButtons();
		MidiState.instance().printCC();
		
		// outgoing midi changes the LED color on the launch control
		MidiDevice.instance().sendMidiOut(true, 0, LaunchControl.PAD_01, P.round(AudioIn.audioFreq(2) * 150f));
		MidiDevice.instance().sendMidiOut(true, 0, LaunchControl.PAD_02, P.round(AudioIn.audioFreq(3) * 150f));
		MidiDevice.instance().sendMidiOut(true, 0, LaunchControl.PAD_03, P.round(AudioIn.audioFreq(4) * 150f));
		MidiDevice.instance().sendMidiOut(true, 0, LaunchControl.PAD_04, P.round(AudioIn.audioFreq(5) * 150f));
		MidiDevice.instance().sendMidiOut(true, 0, LaunchControl.PAD_05, P.round(AudioIn.audioFreq(6) * 150f));
		MidiDevice.instance().sendMidiOut(true, 0, LaunchControl.PAD_06, P.round(AudioIn.audioFreq(7) * 150f));
		MidiDevice.instance().sendMidiOut(true, 0, LaunchControl.PAD_07, P.round(AudioIn.audioFreq(8) * 150f));
		MidiDevice.instance().sendMidiOut(true, 0, LaunchControl.PAD_08, P.round(AudioIn.audioFreq(9) * 150f));
////		MidiDevice.instance().sendMidiOut(true, 0, LaunchControl.PAD_08, P.round(127f * Mouse.xNorm));
//		
//		MidiDevice.instance().sendMidiOut(true, 0, LaunchControl.PAD_08, NovationColors.colors[P.round((NovationColors.colors.length - 1) * Mouse.xNorm)]);
	}
	
	////////////////////////////////////
	// SimpleMidiListener callbacks
	////////////////////////////////////

	public void controllerChange(int channel, int  pitch, int velocity) {
		P.out("CC: ", channel, pitch, velocity);
	}

	public void noteOff(int channel, int  pitch, int velocity) {
		P.out("OFF: ", channel, pitch, velocity);
	}

	public void noteOn(int channel, int pitch, int velocity) {
		P.out("ON: ", channel, pitch, velocity);
	}
	
}